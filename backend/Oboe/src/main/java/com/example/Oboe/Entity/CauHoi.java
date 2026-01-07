package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "cau_hoi")
@Getter
@Setter
public class CauHoi {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_cau_hoi", updatable = false, nullable = false)
    private UUID maCauHoi;

    private String tenCauHoi;
    private String dapAnChinhXac;
    private String luuChon;

    @ManyToOne
    @JoinColumn(name = "maBaiKiemTra")
    private BaiKiemTra baiKiemTra;
}