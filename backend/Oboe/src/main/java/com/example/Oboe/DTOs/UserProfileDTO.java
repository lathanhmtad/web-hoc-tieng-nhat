package com.example.Oboe.DTOs;

import com.example.Oboe.Entity.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UserProfileDTO {
    private UUID maNguoiDung;
    private String userName;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String address;
    private LocalDate day_of_birth;
    private PhuongThucXacThuc authProvider;
    private boolean verified;
    private LoaiTaiKhoan accountType;
    private VaiTro role;
    private TrangThaiTaiKhoan status;
    private LocalDateTime create_at;
    private LocalDateTime update_at;

    // Constructor
    public UserProfileDTO(NguoiDung nguoiDung) {
        this.maNguoiDung = nguoiDung.getMaNguoiDung();
        this.userName = nguoiDung.getEmail();
        this.firstName = nguoiDung.getHo();
        this.lastName = nguoiDung.getTen();
        this.avatarUrl = nguoiDung.getAnhDaiDien();
        this.address = nguoiDung.getDiaChi();
        this.day_of_birth = nguoiDung.getNgaySinh();
        this.authProvider = nguoiDung.getPhuongThucXacThuc();
        this.verified = nguoiDung.isDaXacThuc();
        this.accountType = nguoiDung.getLoaiTaiKhoan();
        this.role = nguoiDung.getVaiTro();
        this.create_at = nguoiDung.getNgayTao();
        this.update_at = nguoiDung.getNgayCapNhat();
        this.status = nguoiDung.getTrangThaiTaiKhoan();
    }
}
