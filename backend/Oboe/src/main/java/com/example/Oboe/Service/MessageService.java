    package com.example.Oboe.Service;


    import com.example.Oboe.DTOs.MessageDTO;
    import com.example.Oboe.DTOs.UserSummaryDTO;
    import com.example.Oboe.Entity.TIN_NHAN;
    import com.example.Oboe.Entity.NGUOI_DUNG;
    import com.example.Oboe.Entity.THONG_BAO;
    import com.example.Oboe.Repository.MessageRepository;
    import com.example.Oboe.Repository.NotificationsRepository;
    import com.example.Oboe.Repository.UserRepository;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.fasterxml.jackson.databind.SerializationFeature;
    import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.stereotype.Service;
    import org.springframework.web.socket.TextMessage;
    import org.springframework.web.socket.WebSocketSession;

    import java.io.IOException;
    import java.time.LocalDateTime;
    import java.time.ZoneId;
    import java.util.*;
    import java.util.stream.Collectors;

    @Service
    public class
    MessageService {


        private final MessageRepository messageRepository;
        private final UserRepository userRepository;
        private final NotificationsRepository notificationsRepository;

        public MessageService(MessageRepository messageRepository, UserRepository userRepository,NotificationsRepository notificationsRepository) {

            this.messageRepository = messageRepository;
            this.userRepository = userRepository;
            this.notificationsRepository = notificationsRepository;

        }
        public MessageDTO sendMessage(UUID senderId, MessageDTO messageDto) {
            // Lấy người gửi từ token
            NGUOI_DUNG sender = userRepository.findById(senderId)
                    .orElseThrow(() -> new RuntimeException("Sender not found"));

            // Lấy người nhận từ DTO
            NGUOI_DUNG receiver = userRepository.findById(messageDto.getReceiverId())
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            // Tạo và lưu message
            TIN_NHAN tinNhan = new TIN_NHAN();
            tinNhan.setNguoiGui(sender);
            tinNhan.setNguoiNhan(receiver);
            tinNhan.setNoiDung(messageDto.getSentMessage());

            ZoneId zoneVN = ZoneId.of("Asia/Ho_Chi_Minh");
            LocalDateTime localDateTimeVN = LocalDateTime.now(zoneVN);
            tinNhan.setThoiGianGui(localDateTimeVN);

            TIN_NHAN savedTinNhan = messageRepository.save(tinNhan);

            // Tạo thông báo
            THONG_BAO notification = new THONG_BAO();
            notification.setNguoiDung(receiver);
            notification.setNoiDung("Bạn nhận được một tin nhắn mới từ " + sender.getEmail());
            notification.setDaDuocDoc(false);
            notification.setThoiGianCapNhat(localDateTimeVN);

            // Gán targetId & targetType cho thông báo
            notification.setMaDoiTuong(savedTinNhan.getMaNguoiNhan());
            notification.setLoaiDoiTuong("Message");

            notificationsRepository.save(notification);

            // Gửi WebSocket đến client
            MessageDTO dto = toDTO(savedTinNhan);

            WebSocketSession receiverSession = SessionManager.getSession(receiver.getMaNguoiDung());

            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                mapper.enable(SerializationFeature.INDENT_OUTPUT);

                String messageWebsocket = mapper.writeValueAsString(dto);
                String notificationWebsocket = mapper.writeValueAsString(notification);

                if (receiverSession != null && receiverSession.isOpen()) {
                    receiverSession.sendMessage(new TextMessage(messageWebsocket));
                    receiverSession.sendMessage(new TextMessage(notificationWebsocket));
                } else {
                    System.out.println("Người nhận không online hoặc đã đóng WebSocket.");
                }
            } catch (IOException e) {
                System.out.println("Lỗi khi gửi WebSocket: " + e.getMessage());
                SessionManager.removeSession(receiver.getMaNguoiDung());
            }

            return dto;
        }




        public List<UserSummaryDTO> getChatPartners(UUID userId) {
            List<UUID> partnerIds = messageRepository.findAllPartnerIds(userId);
            List<NGUOI_DUNG> nguoiDungs = userRepository.findByUserIdIn(partnerIds);
            Pageable limitOne = PageRequest.of(0, 1);

            return nguoiDungs.stream().map(user -> {
                TIN_NHAN lastMsg = messageRepository
                        .findMessageNew(userId, user.getMaNguoiDung(), limitOne)
                        .stream().findFirst().orElse(null);

                return new UserSummaryDTO(
                        user.getMaNguoiDung(),
                        user.getHo(),
                        user.getTen(),
                        user.getEmail(),
                        lastMsg != null ? lastMsg.getNoiDung() : null,
                        lastMsg != null ? lastMsg.getThoiGianGui() : null,
                        user.getAnhDaiDien() //  avatar
                );
            }).collect(Collectors.toList());
        }

        //lấy tất cả cuộc hội thoại
        public List<MessageDTO> getMessagesBetweenUsers(UUID userA, UUID userB) {
            Pageable top30 = PageRequest.of(0, 30); // chỉ lấy 30 tin mới nhất
            List<TIN_NHAN> tinNhans = messageRepository.findConversation(userA, userB,top30);

            Collections.reverse(tinNhans); //  chuyển tin nhắn từ  cũ sang mới

            return tinNhans.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }
        public boolean deleteMessage(UUID messageId, UUID userId) {
            TIN_NHAN tinNhan = getMessage(messageId);
            // Kiểm tra quyền: chỉ sender mới được xóa
            if (!tinNhan.getNguoiGui().getMaNguoiDung().equals(userId)) {
                return false;
            }
            messageRepository.delete(tinNhan);
            return true;
        }

        public TIN_NHAN getMessage(UUID messageId) {
            return messageRepository.findById(messageId).orElse(null);
        }

        private MessageDTO toDTO(TIN_NHAN tinNhan) {
            MessageDTO dto = new MessageDTO();
            dto.setMessageId(tinNhan.getMaTinNhan());
            dto.setSenderId(tinNhan.getNguoiGui().getMaNguoiDung());
            dto.setReceiverId(tinNhan.getNguoiNhan().getMaNguoiDung());
            dto.setSentMessage(tinNhan.getNoiDung());
            dto.setSentDateTime(tinNhan.getThoiGianGui());
            dto.setSenderName(tinNhan.getNguoiGui().getEmail());
            dto.setAvatarUrlSender(tinNhan.getNguoiGui().getAnhDaiDien());
            dto.setAvatarUrlReceiver(tinNhan.getNguoiNhan().getAnhDaiDien());
            return dto;
        }




    }