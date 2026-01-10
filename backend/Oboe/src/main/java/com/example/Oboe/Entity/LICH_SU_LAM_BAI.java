package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "LICH_SU_LAM_BAI")
public class LICH_SU_LAM_BAI {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maKetQua;

    private double diemSo;
    private LocalDateTime thoiGianLamBai;

    @ManyToOne
    @JoinColumn(name = "maBaiKiemTra", nullable = false)
    private BAI_KIEM_TRA baiKiemTra;

    @ManyToOne
    @JoinColumn(name = "maNguoiDung", nullable = false)
    private NGUOI_DUNG nguoiDung;
}
