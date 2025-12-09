package com.example.Oboe.DTOs;

import com.example.Oboe.Entity.LoaiTaiKhoan;
import com.example.Oboe.Entity.PhuongThucXacThuc;
import com.example.Oboe.Entity.VaiTro;
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
    private VaiTro role = VaiTro.ROLE_USER;   // Mặc định là USER
    private boolean verified = false;     // Mặc định chưa xác minh
    private LoaiTaiKhoan accountType = LoaiTaiKhoan.FREE; // Mặc định tài khoản FREE
    private LocalDateTime create_at = LocalDateTime.now();
    private LocalDateTime update_at = LocalDateTime.now();
    private PhuongThucXacThuc authProvider = PhuongThucXacThuc.EMAIL; // Mặc định là EMAIL
}
