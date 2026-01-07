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
@Table(name = "nguoi_dung", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userName", "auth_provider"})
})
public class NguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_nguoi_dung", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID maNguoiDung;

    @Column(nullable = false)
    private String email;

    @JsonIgnore
    @Size(min = 6, message = "Mật khẩu ít nhất 6 ký tự!")
    @Column(nullable = true) // Cho phép null cho tài khoản Firebase/Social login
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
    private VaiTro vaiTro = VaiTro.ROLE_USER;

    @Column(nullable = false)
    private boolean daXacThuc = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoaiTaiKhoan loaiTaiKhoan = LoaiTaiKhoan.FREE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhuongThucXacThuc phuongThucXacThuc;

//    @Column(name = "provider_id")
//    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TrangThaiTaiKhoan trangThaiTaiKhoan = TrangThaiTaiKhoan.ACTION;

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
    private List<BaiViet> baiViets = new ArrayList<>();

    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL)
    @JsonManagedReference("users-Notifications")
    private List<ThongBao> thongBaos = new ArrayList<>();

    // Mối quan hệ 1-nhiều với Comment
    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-comments")
    private List<BinhLuan> binhLuans = new ArrayList<>();

    @OneToMany(mappedBy = "nguoiGui")
    @JsonManagedReference("user-sent-messages")
    private List<TinNhan> tinNhanDaGuis;

    @OneToMany(mappedBy = "nguoiNhan")
    @JsonManagedReference("user-received-messages")
    private List<TinNhan> tinNhanDaNhans;
}
