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
        PhuongThucXacThuc provider = userDTO.getAuthProvider();
        String username = userDTO.getUserName();

        List<NguoiDung> existingNguoiDungs = userRepository.findAllByUserNameAndAuthProvider(username, provider);

        if (!existingNguoiDungs.isEmpty()) {
            if (provider == PhuongThucXacThuc.EMAIL) {
                throw new IllegalStateException("Tài khoản email đã được sử dụng.");
            }
            return existingNguoiDungs.get(0);
        }

        NguoiDung nguoiDung = buildNewUser(userDTO);

        if (provider == PhuongThucXacThuc.EMAIL) {
            validatePassword(userDTO.getPassWord());
            nguoiDung.setMatKhau(passwordEncoder.encode(userDTO.getPassWord()));
        } else {
            nguoiDung.setMatKhau(null);
        }

        return userRepository.save(nguoiDung);
    }


    private NguoiDung buildNewUser(UserDTOs dto) {
        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setEmail(dto.getUserName());
        nguoiDung.setPhuongThucXacThuc(dto.getAuthProvider());
        nguoiDung.setHo(dto.getFirstName());
        nguoiDung.setTen(dto.getLastName());
        nguoiDung.setNgaySinh(dto.getDay_of_birth());
        nguoiDung.setDiaChi(dto.getAddress());
        nguoiDung.setVaiTro(VaiTro.ROLE_USER);
        nguoiDung.setDaXacThuc(dto.isVerified());
        nguoiDung.setLoaiTaiKhoan(LoaiTaiKhoan.FREE);
//        nguoiDung.setProviderId(dto.getProviderId());
        nguoiDung.setNgayTao(LocalDateTime.now());
        nguoiDung.setNgayCapNhat(LocalDateTime.now());
        nguoiDung.setAnhDaiDien(defaultAvatar);
        nguoiDung.setDaXacThuc(true);
        return nguoiDung;
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải ít nhất 6 ký tự.");
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
        nguoiDung.setEmail(dto.getUserName());
        nguoiDung.setHo(dto.getFirstName());
        nguoiDung.setTen(dto.getLastName());
        nguoiDung.setDiaChi(dto.getAddress());
        if(dto.getPassWord() != null && dto.getPassWord() .length() > 6) {
            nguoiDung.setMatKhau(passwordEncoder.encode(dto.getPassWord()));
        }
        nguoiDung.setNgaySinh(dto.getDay_of_birth());
        nguoiDung.setLoaiTaiKhoan(dto.getAccountType());
        nguoiDung.setVaiTro(dto.getRole());
        nguoiDung.setNgayCapNhat(LocalDateTime.now());
        return userRepository.save(nguoiDung);
    }


    // Đổi role
    public NguoiDung changeRole(UUID id, VaiTro newVaiTro) {
        NguoiDung nguoiDung = getUserById(id);
        nguoiDung.setVaiTro(newVaiTro);
        nguoiDung.setNgayCapNhat(LocalDateTime.now());
        return userRepository.save(nguoiDung);
    }

    // Ban hoặc unban
    public NguoiDung updateStatus(UUID id, TrangThaiTaiKhoan trangThaiTaiKhoan) {
        NguoiDung nguoiDung = getUserById(id);
        nguoiDung.setTrangThaiTaiKhoan(trangThaiTaiKhoan);
        nguoiDung.setNgayCapNhat(LocalDateTime.now());
        return userRepository.save(nguoiDung);
    }

    // Reset mật khẩu
    public NguoiDung resetPassword(UUID id, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("Mật khẩu phải ít nhất 8 ký tự");
        }
        NguoiDung nguoiDung = getUserById(id);
        nguoiDung.setMatKhau(passwordEncoder.encode(newPassword));
        nguoiDung.setNgayCapNhat(LocalDateTime.now());
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
