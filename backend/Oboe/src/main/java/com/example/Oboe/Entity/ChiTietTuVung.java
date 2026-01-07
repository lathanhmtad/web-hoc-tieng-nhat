package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "chi_tiet_tu_vung")
public class ChiTietTuVung {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_chi_tiet_tu_vung", updatable = false, nullable = false)
    private UUID maChiTietTuVung;

    @ManyToOne
    @JoinColumn(name = "maTuVung")
    private TuVung tuVung;

    @ManyToOne
    @JoinColumn(name = "maHanTu")
    private HanTu hanTu;

    private int thuTu;
}
