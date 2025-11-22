package com.example.Oboe.Service;

import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.DTOs.PassWordChangeDTOs;
import com.example.Oboe.DTOs.UserDTOs;
import com.example.Oboe.DTOs.UserProfileDTOwithStatistical;
import com.example.Oboe.Entity.LoaiTaiKhoan;
import com.example.Oboe.Entity.AuthProvider;
import com.example.Oboe.Entity.NguoiDung;
import com.example.Oboe.Entity.VaiTro;
import com.example.Oboe.Repository.BlogRepository;
import com.example.Oboe.Repository.CommentRepository;
import com.example.Oboe.Repository.FlashCardRepository;
import com.example.Oboe.Repository.UserRepository;
import com.example.Oboe.Util.VerificationHolder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class UserService implements UserDetailsService {
    @Value("${app.default-avatar}")
    private String defaultAvatar;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final BlogRepository blogRepository;
    private final CommentRepository commentRepository;
    private final FlashCardRepository flashCardRepository;
    @Value("${app.domain}")
    private String domain;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MailService mailService ,BlogRepository blogRepository,CommentRepository commentRepository,
                       FlashCardRepository flashCardRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.blogRepository =blogRepository;
        this.commentRepository =commentRepository;
        this.flashCardRepository =flashCardRepository;

    }
    @Autowired
    private S3Service s3Service;

    public void registerWithEmail(UserDTOs userDTOs) {
        String username = userDTOs.getUserName();

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống.");
        }

        if (isValidEmail(username)) {
            // Gửi email xác minh
            String verificationToken = UUID.randomUUID().toString();
            VerificationHolder.getInstance().addToken(verificationToken, userDTOs);

            String verificationLink = domain+"/api/auth/verify?token=" + verificationToken;

            mailService.sendMail(username, "Xác minh tài khoản",
                    "Click vào liên kết để xác minh tài khoản của bạn: " + verificationLink);
        } else if (isValidPhone(username)) {
            // Không cần gửi email, xác minh luôn
            userDTOs.setVerified(true);
            addUser(userDTOs);
        } else {
            throw new IllegalArgumentException("Tên đăng nhập phải là email hoặc số điện thoại hợp lệ.");
        }
    }



    public NguoiDung verifyAccount(String token) {
        UserDTOs signupRequest = VerificationHolder.getInstance().getSignupRequest(token);
        if (signupRequest == null) throw new IllegalArgumentException("Invalid or expired verification token.");
        signupRequest.setVerified(true);
        NguoiDung nguoiDung = addUser(signupRequest);
        VerificationHolder.getInstance().removeToken(token);
        return nguoiDung;
    }

    public NguoiDung addUser(UserDTOs userDTOs) {
        AuthProvider provider = userDTOs.getAuthProvider();
        String username = userDTOs.getUserName();

        // Cho phép trùng username nếu khác provider
        List<NguoiDung> existingNguoiDungs = userRepository.findAllByUserNameAndAuthProvider(username, provider);
        if (!existingNguoiDungs.isEmpty()) {
            if (provider == AuthProvider.EMAIL) {
                throw new IllegalStateException("Tài khoản email đã được sử dụng.");
            } else {
                return existingNguoiDungs.get(0); // Google/Facebook → dùng lại
            }
        }

        // Tạo mới nếu chưa tồn tại
        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setUserName(username);
        nguoiDung.setAuthProvider(provider);
        nguoiDung.setFirstName(userDTOs.getFirstName());
        nguoiDung.setLastName(userDTOs.getLastName());
        nguoiDung.setDay_of_birth(userDTOs.getDay_of_birth());
        nguoiDung.setAddress(userDTOs.getAddress());
        nguoiDung.setRole(VaiTro.ROLE_USER);
        nguoiDung.setVerified(userDTOs.isVerified());
        nguoiDung.setAccountType(LoaiTaiKhoan.FREE);
        nguoiDung.setProviderId(userDTOs.getProviderId());
        nguoiDung.setCreate_at(LocalDateTime.now());
        nguoiDung.setUpdate_at(LocalDateTime.now());
        nguoiDung.setAvatarUrl(defaultAvatar);
        if (provider == AuthProvider.EMAIL) {
            if (userDTOs.getPassWord() == null || userDTOs.getPassWord().length() < 8) {
                throw new IllegalArgumentException("Mật khẩu phải ít nhất 8 ký tự.");
            }
            nguoiDung.setPassWord(passwordEncoder.encode(userDTOs.getPassWord()));
        } else {
            nguoiDung.setPassWord(null);
        }

        return userRepository.save(nguoiDung);
    }


    public List<NguoiDung> findByUserName(String userName) {
        return userRepository.searchUsers(userName);
    }


    public List<NguoiDung> findByUserNameAndAuthProvider(String userName, AuthProvider provider) {
        return userRepository.findAllByUserNameAndAuthProvider(userName, provider);
    }

    public UserProfileDTOwithStatistical getUserByIds(UUID userId) {
        NguoiDung nguoiDung = userRepository.findByUser_id(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Thống kê số lượng nội dung của user
        long blogCount = blogRepository.countBlogsByUserId(userId);
        long commentCount = commentRepository.countCommentsByUserId(userId);
        long flashCardCount = flashCardRepository.countFlashCardByUserId(userId);

        // Tạo DTO với cả thông tin người dùng và thống kê
        UserProfileDTOwithStatistical dto = new UserProfileDTOwithStatistical(nguoiDung);
        dto.setBlogCount((int) blogCount);
        dto.setCommentCount((int) commentCount);
        dto.setFlashCardCount((int) flashCardCount);

        return dto;
    }

    public void deleteUser(UUID userId) {
        NguoiDung nguoiDung = userRepository.findByUser_id(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Xóa comment của user
        commentRepository.deleteUserbyComment(userId);

        // Xóa blog của user
        blogRepository.deleteBlogsbyUser(userId);

        // Xóa flashcard của user
        flashCardRepository.deleteUser(userId);

        // xóa user
        userRepository.delete(nguoiDung);
    }



    public List<NguoiDung> findAllByUserName(String userName) {
        return userRepository.searchUsers(userName);
    }

    public UserDetails loadUserByUsername(String username) {
        List<NguoiDung> nguoiDungs = userRepository.findAllByUserNameAndAuthProvider(username, AuthProvider.EMAIL);

        if (nguoiDungs.isEmpty()) {
            throw new UsernameNotFoundException("User not found (EMAIL)");
        }
        if (nguoiDungs.size() > 1) {
            throw new UsernameNotFoundException("Tồn tại nhiều người dùng trùng thông tin đăng nhập.");
        }

        NguoiDung nguoiDung = nguoiDungs.get(0);

        if (!nguoiDung.isVerified()) {
            throw new UsernameNotFoundException("Tài khoản chưa xác minh email.");
        }

        if (nguoiDung.getStatus() != null && nguoiDung.getStatus().toString().equalsIgnoreCase("BANNED")) {
            throw new UsernameNotFoundException("Tài khoản đã bị khóa.");
        }

        return buildPrincipal(nguoiDung);
    }


    public UserDetails loadUserByUsernameAndProvider(String username, AuthProvider provider) {
        List<NguoiDung> nguoiDungs = userRepository.findAllByUserNameAndAuthProvider(username, provider);

        if (nguoiDungs.isEmpty()) {
            throw new UsernameNotFoundException("User not found (" + provider + ")");
        }
        if (nguoiDungs.size() > 1) {
            throw new UsernameNotFoundException("Tồn tại nhiều người dùng trùng thông tin đăng nhập.");
        }

        NguoiDung nguoiDung = nguoiDungs.get(0);

        if (nguoiDung.getStatus() != null && nguoiDung.getStatus().toString().equalsIgnoreCase("BANNED")) {
            throw new UsernameNotFoundException("Tài khoản đã bị khóa.");
        }

        return buildPrincipal(nguoiDung);
    }


    private UserDetails buildPrincipal(NguoiDung nguoiDung) {
        String password = nguoiDung.getPassWord();

        if (nguoiDung.getAuthProvider() == AuthProvider.EMAIL && (password == null || password.isBlank())) {
            throw new UsernameNotFoundException("Password is missing for email login.");
        }

        return new CustomUserDetails(nguoiDung);
    }


    public NguoiDung updateMyOwnProfile(String username, AuthProvider authProvider, UserDTOs userDTOs) {
        List<NguoiDung> nguoiDungs = userRepository.findAllByUserNameAndAuthProvider(username, authProvider);

        if (nguoiDungs.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        if (nguoiDungs.size() > 1) {
            throw new UsernameNotFoundException("Trùng thông tin tài khoản.");
        }

        NguoiDung nguoiDung = nguoiDungs.get(0);

        nguoiDung.setFirstName(userDTOs.getFirstName());
        nguoiDung.setLastName(userDTOs.getLastName());
        nguoiDung.setAddress(userDTOs.getAddress());
        nguoiDung.setDay_of_birth(userDTOs.getDay_of_birth());
        nguoiDung.setUpdate_at(LocalDateTime.now());

        return userRepository.save(nguoiDung);
    }



    public void changePassword(String username, PassWordChangeDTOs passWordChange) {
        List<NguoiDung> nguoiDungs = userRepository.findAllByUserNameAndAuthProvider(username, AuthProvider.EMAIL);

        if (nguoiDungs.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        if (nguoiDungs.size() > 1) {
            throw new IllegalStateException("Tồn tại nhiều người dùng với cùng username.");
        }

        NguoiDung nguoiDung = nguoiDungs.get(0);

        if (nguoiDung.getAuthProvider() != AuthProvider.EMAIL) {
            throw new IllegalArgumentException("Không thể đổi mật khẩu với tài khoản Google/Facebook.");
        }

        if (!passwordEncoder.matches(passWordChange.getOldPassword(), nguoiDung.getPassWord())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        validatePassword(passWordChange.getNewPassword());

        nguoiDung.setPassWord(passwordEncoder.encode(passWordChange.getNewPassword()));
        userRepository.save(nguoiDung);
    }


//    public UserDTOs convertOAuthToDTO(String email, String firstName, String lastName, AuthProvider provider) {
//        UserDTOs dto = new UserDTOs();
//        dto.setUserName(email);
//        dto.setFirstName(firstName);
//        dto.setLastName(lastName);
//        dto.setVerified(true);
//        dto.setPassWord(null);
//        dto.setRole(Role.ROLE_USER);
//        dto.setAccountType(AccountType.FREE);
//        dto.setCreate_at(LocalDateTime.now());
//        dto.setUpdate_at(LocalDateTime.now());
//        dto.setAuthProvider(provider);
//        return dto;
//    }



    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank.");
        }
        if (!isStrongPassword(password)) {
            throw new IllegalArgumentException("Password is too weak");
        }
    }

    private boolean isStrongPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[!@#$%^&*()].*");
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^\\+?[0-9]{10,15}$");
    }


    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public NguoiDung getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }


    public NguoiDung uploadAvatarForUser(String username, AuthProvider provider, MultipartFile file) throws Exception {
        List<NguoiDung> nguoiDungs = userRepository.findAllByUserNameAndAuthProvider(username, provider);

        if (nguoiDungs.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        if (nguoiDungs.size() > 1) {
            throw new IllegalStateException("Tồn tại nhiều người dùng trùng username + provider.");
        }

        NguoiDung nguoiDung = nguoiDungs.get(0);
        String avatarUrl = s3Service.uploadFile(file, "upload-avatar/");
        nguoiDung.setAvatarUrl(avatarUrl);
        nguoiDung.setUpdate_at(LocalDateTime.now());
        return userRepository.save(nguoiDung);
    }

    public Optional<NguoiDung> findById(UUID id) {
        return userRepository.findById(id);
    }

    public NguoiDung saveUser(NguoiDung nguoiDung) {
        return userRepository.save(nguoiDung);
    }


}