package com.example.Oboe.DTOs;

import com.example.Oboe.Entity.LOAI_TAI_KHOAN;
import com.example.Oboe.Entity.PHUONG_THUC_XAC_THUC;
import com.example.Oboe.Entity.VAI_TRO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserDTOs {

    private String userName; // Có thể là email, sdt hoặc username
    private String passWord; // Chỉ sử dụng khi đăng nhập bằng EMAIL
    private String lastName;
    private String firstName;
    private LocalDate day_of_birth;
    private String address;
    private String avatar;
//    private String providerId;
    private VAI_TRO role = VAI_TRO.ROLE_USER;   // Mặc định là USER
    private boolean verified = false;     // Mặc định chưa xác minh
    private LOAI_TAI_KHOAN accountType = LOAI_TAI_KHOAN.FREE; // Mặc định tài khoản FREE
    private LocalDateTime create_at = LocalDateTime.now();
    private LocalDateTime update_at = LocalDateTime.now();
    private PHUONG_THUC_XAC_THUC authProvider = PHUONG_THUC_XAC_THUC.EMAIL; // Mặc định là EMAIL
}
