package com.example.Oboe.Controller;

import com.example.Oboe.Entity.NguoiDung;
import com.example.Oboe.Entity.PhuongThucXacThuc;
import com.google.firebase.auth.FirebaseAuthException;
import com.example.Oboe.DTOs.LoginRequest;
import com.example.Oboe.DTOs.PassWordChangeDTOs;
import com.example.Oboe.DTOs.UserDTOs;
import com.example.Oboe.DTOs.FirebaseLoginRequest;
import com.example.Oboe.Service.UserService;
import com.example.Oboe.Service.FirebaseService;
import com.example.Oboe.Util.JwtUtil;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final FirebaseService firebaseService;

    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil, FirebaseService firebaseService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.firebaseService = firebaseService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDTOs userDTOs) {
        if (userDTOs.getUserName() == null || userDTOs.getUserName().isEmpty()) {
            return ResponseEntity.badRequest().body("Username is required.");
        }

        if (userDTOs.getPassWord() == null || userDTOs.getPassWord().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required.");
        }

        PhuongThucXacThuc currentProvider = userDTOs.getAuthProvider() != null ? userDTOs.getAuthProvider() : PhuongThucXacThuc.EMAIL;
        List<NguoiDung> existingNguoiDungs = userService.findByUserName(userDTOs.getUserName());

        for (NguoiDung existing : existingNguoiDungs) {
            if (existing.getPhuongThucXacThuc() == currentProvider) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Tài khoản đã tồn tại với nhà cung cấp " + currentProvider);
            }
        }

        // gửi mail xác thực
        userService.registerWithEmail(userDTOs);
        return ResponseEntity.ok("Verification email sent. Please check your email.");
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam("token") String token) {
        try {
            userService.verifyAccount(token);
            return ResponseEntity.ok("Account verified successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassWord();

        // Kiểm tra người dùng có tồn tại
        List<NguoiDung> nguoiDungList = userService.findByUserNameAndAuthProvider(username, PhuongThucXacThuc.EMAIL);
        if (nguoiDungList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        if (nguoiDungList.size() > 1) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nhiều tài khoản trùng username và provider.");
        }
        NguoiDung nguoiDung = nguoiDungList.get(0);


        // Không cho đăng nhập nếu chưa xác minh
        if (!nguoiDung.isDaXacThuc()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Please verify your email before logging in.");
        }

        // Nếu là tài khoản Google/Facebook, không cho đăng nhập password
        if (nguoiDung.getPhuongThucXacThuc() != PhuongThucXacThuc.EMAIL) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Hãy đăng nhập bằng " + nguoiDung.getPhuongThucXacThuc());
        }

        try {
            // Thực hiện xác thực Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Lấy userDetails từ authentication và sinh JWT
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails, PhuongThucXacThuc.EMAIL.name());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful!");
            response.put("token", jwt);
            response.put("user", Map.of(
                    "username", nguoiDung.getEmail(),
                    "firstName", nguoiDung.getHo(),
                    "lastName", nguoiDung.getTen(),
                    "role", nguoiDung.getVaiTro().name(),
                    "displayName", nguoiDung.getHo() + " " + nguoiDung.getTen(),
                    "avatarUrl", nguoiDung.getAnhDaiDien(), // Thêm avatarUrl
                    "photoURL", nguoiDung.getAnhDaiDien() != null && !nguoiDung.getAnhDaiDien().isBlank()
                            ? nguoiDung.getAnhDaiDien()
                            : "https://ui-avatars.com/api/?name=" + nguoiDung.getHo() + "+" + nguoiDung.getTen()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        }
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(@RequestBody UserDTOs userDTOs, Authentication authentication) {
        String username = authentication.getName();

        try {
            NguoiDung updatedNguoiDung = userService.updateMyOwnProfile(username, PhuongThucXacThuc.EMAIL, userDTOs);
            return ResponseEntity.ok(updatedNguoiDung);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody PassWordChangeDTOs passwordChange,
                                            Authentication authentication) {
        String username = authentication.getName();

        try {
            userService.changePassword(username, passwordChange);
            return ResponseEntity.ok("Password changed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Tài khoản và dữ liệu liên quan đã được xóa.");
    }

    @PostMapping("/uploadAvatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file,
                                          Authentication authentication) {
        String username = authentication.getName();

        try {
            NguoiDung nguoiDung = userService.uploadAvatarForUser(username, PhuongThucXacThuc.EMAIL, file);
            return ResponseEntity.ok(Map.of("avatarUrl", nguoiDung.getAnhDaiDien()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thiếu Authorization header");
        }

        String token = authHeader.substring(7); // Bỏ "Bearer "

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
        }

        // Lấy username (sub) và provider từ JWT
        String username = jwtUtil.getEmailFromToken(token);
        String providerStr = jwtUtil.getProviderFromToken(token);

        PhuongThucXacThuc provider;
        try {
            provider = PhuongThucXacThuc.valueOf(providerStr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provider không hợp lệ: " + providerStr);
        }

        NguoiDung nguoiDung = userService.findByUserNameAndAuthProvider(username, provider)
                .stream().findFirst().orElse(null);

        if (nguoiDung == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy user với username = " + username + " và provider = " + provider);
        }

        Map<String, Object> response = Map.of(
                "user", Map.of(
                        "username", nguoiDung.getEmail(),
                        "firstName", nguoiDung.getHo(),
                        "lastName", nguoiDung.getTen(),
                        "role", nguoiDung.getVaiTro().name(),
                        "displayName", nguoiDung.getHo() + " " + nguoiDung.getTen(),
                        "avatarUrl", nguoiDung.getAnhDaiDien(), // Thêm avatarUrl
                        "photoURL", nguoiDung.getAnhDaiDien() != null && !nguoiDung.getAnhDaiDien().isBlank()
                                ? nguoiDung.getAnhDaiDien()
                                : "https://ui-avatars.com/api/?name=" + nguoiDung.getHo() + "+" + nguoiDung.getTen()
                )
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/loginWithFirebase")
    public ResponseEntity<?> loginWithFirebase(@RequestBody FirebaseLoginRequest request) {

        if (request == null || request.getIdToken() == null || request.getIdToken().trim().isEmpty()) {
            System.out.println("ERROR: Missing or empty ID token");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Missing or empty Firebase ID token");
        }

        try {
            FirebaseToken decodedToken = firebaseService.verifyIdToken(request.getIdToken());
            NguoiDung nguoiDung = firebaseService.processFirebaseUser(decodedToken);
            UserDetails userDetails = userService.loadUserByUsernameAndProvider(
                    nguoiDung.getEmail(), nguoiDung.getPhuongThucXacThuc());
            String jwt = jwtUtil.generateToken(userDetails, nguoiDung.getPhuongThucXacThuc().name());
            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Firebase login successful!");
            response.put("token", jwt);
            response.put("user", Map.of(
                    "username", nguoiDung.getEmail(),
                    "firstName", nguoiDung.getHo(),
                    "lastName", nguoiDung.getTen(),
                    "role", nguoiDung.getVaiTro().name(),
                    "displayName", nguoiDung.getHo() + " " + nguoiDung.getTen(),
                    "avatarUrl", nguoiDung.getAnhDaiDien(), // Thêm avatarUrl
                    "photoURL", nguoiDung.getAnhDaiDien() != null && !nguoiDung.getAnhDaiDien().isBlank()
                            ? nguoiDung.getAnhDaiDien()
                            : "https://ui-avatars.com/api/?name=" + nguoiDung.getHo() + "+" + nguoiDung.getTen()
            ));

            return ResponseEntity.ok(response);

        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Firebase token: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR: General Exception - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Firebase authentication failed: " + e.getMessage());
        }
    }
}
