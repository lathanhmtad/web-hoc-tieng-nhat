package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "ket_qua")
public class KetQua {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_ket_qua", updatable = false, nullable = false)
    private UUID maKetQua;

    private double diemSo;
    private LocalDateTime thoiGianLamBai;

    @ManyToOne
    @JoinColumn(name = "maBaiKiemTra", nullable = false)
    private BaiKiemTra baiKiemTra;
}
