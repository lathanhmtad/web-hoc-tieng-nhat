package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "DON_HANG")
public class DON_HANG {
    @Id
    @Column(updatable = false, nullable = false)
    private Long maDonHang; // Đây sẽ đóng vai trò là Mã đơn hàng (vnp_TxnRef)

    private String maGiaoDich; // Mã từ cổng thanh toán (vnp_TransactionNo)
    private BigDecimal soTien;

    @Enumerated(EnumType.STRING)
    private TRANG_THAI_THANH_TOAN trangThai; // PENDING, SUCCESS, FAILED

    private LocalDateTime thoiGianThanhToan;

    @Enumerated(EnumType.STRING)
    private PHUONG_THUC_THANH_TOAN phuongThucThanhToan;

    private LocalDateTime ngayHetHan;

    @ManyToOne
    @JoinColumn(name = "maNguoiDung", nullable = false)
    @JsonBackReference("users-Payment")
    private NGUOI_DUNG nguoiDung;
}
