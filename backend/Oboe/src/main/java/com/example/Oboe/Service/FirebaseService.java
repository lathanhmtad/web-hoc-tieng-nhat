package com.example.Oboe.Service;

import com.example.Oboe.Entity.NguoiDung;
import com.example.Oboe.Entity.PhuongThucXacThuc;
import com.example.Oboe.Entity.TrangThaiTaiKhoan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.example.Oboe.DTOs.UserDTOs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class FirebaseService {

    @Autowired
    private UserService userService;

    public FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }

    public NguoiDung processFirebaseUser(FirebaseToken decodedToken) {
        String email = decodedToken.getEmail();
        String name = decodedToken.getName();
        String picture = decodedToken.getPicture();
//        String providerId = decodedToken.getUid();
        
        // Xác định provider dựa trên firebase provider
        Object firebaseObj = decodedToken.getClaims().get("firebase");
        PhuongThucXacThuc phuongThucXacThuc = PhuongThucXacThuc.GOOGLE; // Default
        
        if (firebaseObj != null) {
            Map<String, Object> firebaseData = (Map<String, Object>) firebaseObj;
            Object identitiesObj = firebaseData.get("identities");
            if (identitiesObj != null) {
                Map<String, Object> identities = (Map<String, Object>) identitiesObj;
                if (identities.containsKey("facebook.com")) {
                    phuongThucXacThuc = PhuongThucXacThuc.FACEBOOK;
                } else if (identities.containsKey("google.com")) {
                    phuongThucXacThuc = PhuongThucXacThuc.GOOGLE;
                }
            }
        }
        
        // Tách tên thành firstName và lastName
        String firstName = "";
        String lastName = "";
        if (name != null && !name.isEmpty()) {
            String[] nameParts = name.split(" ", 2);
            firstName = nameParts[0];
            if (nameParts.length > 1) {
                lastName = nameParts[1];
            }
        }

        // Kiểm tra user đã tồn tại chưa
        var existingUsers = userService.findByUserNameAndAuthProvider(email, phuongThucXacThuc);
        if (!existingUsers.isEmpty()) {
            NguoiDung existingNguoiDung = existingUsers.get(0);

            if (existingNguoiDung.getTrangThaiTaiKhoan() == TrangThaiTaiKhoan.BAN) {
                throw new RuntimeException("User is banned");
            }
            if (picture != null && !picture.isEmpty()) {
                existingNguoiDung.setAnhDaiDien(picture);
                return userService.saveUser(existingNguoiDung);
            }
            return existingNguoiDung;
        }

        // Tạo user mới
        UserDTOs userDTO = new UserDTOs();
        userDTO.setUserName(email);
        userDTO.setFirstName(firstName);
        userDTO.setLastName(lastName);
        userDTO.setAuthProvider(phuongThucXacThuc);
//        userDTO.setProviderId(providerId);
        userDTO.setVerified(true); // Firebase users are already verified
        userDTO.setPassWord(null); // No password for OAuth users

        NguoiDung newNguoiDung = userService.addUser(userDTO);
        
        // Set avatar URL if available
        if (picture != null && !picture.isEmpty()) {
            newNguoiDung.setAnhDaiDien(picture);
            return userService.saveUser(newNguoiDung);
        }
        
        return newNguoiDung;
    }
}