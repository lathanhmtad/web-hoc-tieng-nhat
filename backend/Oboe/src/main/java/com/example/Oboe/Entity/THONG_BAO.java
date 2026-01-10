package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "THONG_BAO")
public class THONG_BAO {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maThongBao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maNguoiDung", nullable = false)
    @JsonBackReference("users-Notifications")
    private NGUOI_DUNG nguoiDung;

    private String noiDung;

    private boolean daDuocDoc;

    private UUID maDoiTuong;

    private String loaiDoiTuong;

    private LocalDateTime thoiGianCapNhat = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.thoiGianCapNhat = LocalDateTime.now();
    }
}
