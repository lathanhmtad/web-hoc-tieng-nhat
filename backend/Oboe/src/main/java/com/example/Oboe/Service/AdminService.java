package com.example.Oboe.Service;

import com.example.Oboe.DTOs.UserDTOs;
import com.example.Oboe.DTOs.UserProfileDTO;
import com.example.Oboe.Entity.*;
import com.example.Oboe.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Transactional
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final BlogRepository blogRepository;
    private final CommentRepository commentRepository;
    private final FlashCardRepository flashCardRepository;
    @Value("${app.default-avatar}")
    private String defaultAvatar;

    @Value("${app.domain}")
    private String domain;

    @Autowired
    private UserRepositoryCustomImpl userRepositoryCustomImpl;
    @Autowired
    public AdminService(UserRepository userRepository, BlogRepository blogRepository, CommentRepository commentRepository,
                        FlashCardRepository flashCardRepository,
                        PasswordEncoder passwordEncoder,
                        MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.blogRepository = blogRepository;
        this.commentRepository = commentRepository;
        this.flashCardRepository = flashCardRepository;
    }

    // Tạo tài khoản mới (Admin hoặc User)
    public NguoiDung createUser(UserDTOs userDTO) {
        AuthProvider provider = userDTO.getAuthProvider();
        String username = userDTO.getUserName();

        List<NguoiDung> existingNguoiDungs = userRepository.findAllByUserNameAndAuthProvider(username, provider);

        if (!existingNguoiDungs.isEmpty()) {
            if (provider == AuthProvider.EMAIL) {
                throw new IllegalStateException("Tài khoản email đã được sử dụng.");
            }
            return existingNguoiDungs.get(0);
        }

        NguoiDung nguoiDung = buildNewUser(userDTO);

        if (provider == AuthProvider.EMAIL) {
            validatePassword(userDTO.getPassWord());
            nguoiDung.setPassWord(passwordEncoder.encode(userDTO.getPassWord()));
        } else {
            nguoiDung.setPassWord(null);
        }

        return userRepository.save(nguoiDung);
    }


    private NguoiDung buildNewUser(UserDTOs dto) {
        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setUserName(dto.getUserName());
        nguoiDung.setAuthProvider(dto.getAuthProvider());
        nguoiDung.setFirstName(dto.getFirstName());
        nguoiDung.setLastName(dto.getLastName());
        nguoiDung.setDay_of_birth(dto.getDay_of_birth());
        nguoiDung.setAddress(dto.getAddress());
        nguoiDung.setRole(VaiTro.ROLE_USER);
        nguoiDung.setVerified(dto.isVerified());
        nguoiDung.setAccountType(LoaiTaiKhoan.FREE);
        nguoiDung.setProviderId(dto.getProviderId());
        nguoiDung.setCreate_at(LocalDateTime.now());
        nguoiDung.setUpdate_at(LocalDateTime.now());
        nguoiDung.setAvatarUrl(defaultAvatar);
        nguoiDung.setVerified(true);
        return nguoiDung;
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Mật khẩu phải ít nhất 8 ký tự.");
        }
    }



    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(regex, email);
    }


    private boolean isValidPhone(String phone) {
        String regex = "^\\+?[0-9]{10,15}$";
        return Pattern.matches(regex, phone);
    }

    // Cập nhật người dùng
    public NguoiDung updateUser(UUID id, UserDTOs dto) {
        NguoiDung nguoiDung = getUserById(id);
        nguoiDung.setFirstName(dto.getFirstName());
        nguoiDung.setLastName(dto.getLastName());
        nguoiDung.setAddress(dto.getAddress());
        nguoiDung.setDay_of_birth(dto.getDay_of_birth());
        nguoiDung.setAccountType(dto.getAccountType());
        nguoiDung.setRole(dto.getRole());
        nguoiDung.setUpdate_at(LocalDateTime.now());
        return userRepository.save(nguoiDung);
    }

    // Xoá người dùng
//    public void deleteUser(UUID id) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng."));
//
//        commentRepository.deleteUserbyComment(id);
//
//        blogRepository.deleteBlogsbyUser(id);
//
//
//        userRepository.delete(user);
//    }


    // Đổi role
    public NguoiDung changeRole(UUID id, VaiTro newVaiTro) {
        NguoiDung nguoiDung = getUserById(id);
        nguoiDung.setRole(newVaiTro);
        nguoiDung.setUpdate_at(LocalDateTime.now());
        return userRepository.save(nguoiDung);
    }

    // Ban hoặc unban
    public NguoiDung updateStatus(UUID id, Status status) {
        NguoiDung nguoiDung = getUserById(id);
        nguoiDung.setStatus(status);
        nguoiDung.setUpdate_at(LocalDateTime.now());
        return userRepository.save(nguoiDung);
    }

    // Reset mật khẩu
    public NguoiDung resetPassword(UUID id, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("Mật khẩu phải ít nhất 8 ký tự");
        }
        NguoiDung nguoiDung = getUserById(id);
        nguoiDung.setPassWord(passwordEncoder.encode(newPassword));
        nguoiDung.setUpdate_at(LocalDateTime.now());
        return userRepository.save(nguoiDung);
    }

    // Lấy toàn bộ người dùng
    public List<NguoiDung> getAllUsers() {
        return userRepository.findAll();
    }

    // Lấy người dùng theo ID
    public NguoiDung getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với id: " + id));
    }
    public List<UserProfileDTO> getAllUserProfiles() {
        return userRepository.findAll()
                .stream()
                .map(UserProfileDTO::new)
                .toList();
    }
    // Tìm kiếm theo từ khóa
    public List<NguoiDung> searchUsers(String keyword) {
        return userRepository.searchUsers(keyword);
    }

    public List<NguoiDung> findByUserName(String userName) {
        return userRepository.searchUsers(userName);
    }


    public Optional<NguoiDung> findById(UUID id) {
        return userRepository.findById(id);
    }

    public void deleteUser(UUID userId) {
        userRepositoryCustomImpl.deleteUserWithDependencies(userId);
    }
}
