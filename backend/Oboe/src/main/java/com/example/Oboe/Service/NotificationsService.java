package com.example.Oboe.Service;

import com.example.Oboe.DTOs.NotificationsDTO;
import com.example.Oboe.Entity.THONG_BAO;
import com.example.Oboe.Repository.NotificationsRepository;
import com.example.Oboe.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationsService {

    private final UserRepository userRepository;
    private final NotificationsRepository notificationsRepository;

    public NotificationsService(UserRepository userRepository, NotificationsRepository notificationsRepository) {
        this.userRepository = userRepository;
        this.notificationsRepository = notificationsRepository;
    }

    public List<NotificationsDTO> getAllNotification(UUID userId) {
        Pageable top30 = PageRequest.of(0, 30); // chỉ lấy 30 thông báo mới nhất
        List<THONG_BAO> notifications = notificationsRepository.findConversation(userId, top30);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public int markAllNotificationsAsRead(UUID userId) {
        return notificationsRepository.markAllAsRead(userId);
    }

    @Transactional
    public boolean markNotificationAsRead(UUID notificationId) {
        THONG_BAO read = notificationsRepository.findById(notificationId).orElse(null);
        if (read != null && !read.isDaDuocDoc()) {
            read.setDaDuocDoc(true); // Đánh dấu đã đọc
            notificationsRepository.save(read); // Lưu lại
            return true;
        }
        return false;
    }

    public NotificationsDTO convertToDTO(THONG_BAO thongBao) {
        return new NotificationsDTO(
                thongBao.getMaThongBao(),
                thongBao.getNguoiDung().getMaNguoiDung(),
                thongBao.getNoiDung(),
                thongBao.isDaDuocDoc(),
                thongBao.getThoiGianCapNhat(),
                thongBao.getMaDoiTuong(),     // ← thêm mới
                thongBao.getLoaiDoiTuong()    // ← thêm mới
        );
    }
}
