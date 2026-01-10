package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "CHI_TIET_TU_VUNG")
public class CHI_TIET_TU_VUNG {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maChiTietTuVung;

    @ManyToOne
    @JoinColumn(name = "maTuVung")
    private TU_VUNG tuVung;

    @ManyToOne
    @JoinColumn(name = "maHanTu")
    private HAN_TU hanTu;

    private int thuTu;
}
