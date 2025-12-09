package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name="don_hang")
public class DonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "ma_don_hang")
    private Long maDonHang;

    @Column(name = "ma_giao_dich")
    private String maGiaoDich;

    private int soTien;

    private String trangThai;

    private LocalDate ngayGiaoDich = LocalDate.now();

    @Column(name = "thoi_gian_thanh_toan")
    private LocalDateTime thoiGianThanhToan;

    @Column(name = "phuong_thuc_thanh_toan")
    private String phuongThucThanhToan;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    @JsonBackReference("users-Payment")
    private NguoiDung nguoiDung;
}
