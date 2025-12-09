package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "tu_vung_han_tu")
@Getter
@Setter
public class ChiTietTuVung {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_tu_vung_han_tu")
    private UUID maTuVungHanTu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tu_vung_id")
    private TuVung tuVung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "han_tu_id")
    private HanTu hanTu;

    @Column(name = "thu_tu")
    private int thuTu;
}
