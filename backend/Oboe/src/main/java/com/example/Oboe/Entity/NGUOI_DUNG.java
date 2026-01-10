package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@Entity
@Table(name = "NGUOI_DUNG")
public class NGUOI_DUNG {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID maNguoiDung;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Size(min = 6, message = "Mật khẩu ít nhất 6 ký tự!")
    @Column // Cho phép null cho tài khoản Firebase/Social login
    private String matKhau;

    @Column(nullable = false)
    private String ho;

    @Column(nullable = false)
    private String ten;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate ngaySinh;

    private String diaChi;

    private String anhDaiDien;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VAI_TRO vaiTro = VAI_TRO.ROLE_USER;

    @Column(nullable = false)
    private boolean daXacThuc = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LOAI_TAI_KHOAN loaiTaiKhoan = LOAI_TAI_KHOAN.FREE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PHUONG_THUC_XAC_THUC phuongThucXacThuc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TRANG_THAI_TAI_KHOAN trangThaiTaiKhoan = TRANG_THAI_TAI_KHOAN.ACTION;

    @Column(nullable = false, updatable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime ngayCapNhat = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.ngayCapNhat = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-blogs")
    private List<BAI_VIET> baiViets = new ArrayList<>();

    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL)
    @JsonManagedReference("users-Notifications")
    private List<THONG_BAO> thongBaos = new ArrayList<>();

    // Mối quan hệ 1-nhiều với Comment
    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-comments")
    private List<BINH_LUAN> binhLuans = new ArrayList<>();

    @OneToMany(mappedBy = "nguoiGui")
    @JsonManagedReference("user-sent-messages")
    private List<TIN_NHAN> tinNhanDaGuis;

    @OneToMany(mappedBy = "nguoiNhan")
    @JsonManagedReference("user-received-messages")
    private List<TIN_NHAN> tinNhanDaNhans;
}
