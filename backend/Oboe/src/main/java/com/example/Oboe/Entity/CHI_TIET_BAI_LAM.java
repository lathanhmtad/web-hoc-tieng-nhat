package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "CHI_TIET_BAI_LAM")
public class CHI_TIET_BAI_LAM {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maChiTietBaiLam;

    private String noiDungTraLoi;
    private boolean laDapAnDung;

    @ManyToOne
    @JoinColumn(name = "maCauHoi", nullable = false)
    private CAU_HOI cauHoi;

    @ManyToOne
    @JoinColumn(name = "maLichSuBaiLam", nullable = false)
    private LICH_SU_LAM_BAI lichSuLamBai;
}
