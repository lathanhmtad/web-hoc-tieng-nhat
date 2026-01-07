package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "yeu_thich")
public class YeuThich {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "FavoritesID", updatable = false, nullable = false)
    private UUID FavoritesID;
    private String tieuDe;
    private String noiDung;
    private LocalDate ngayTao = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "maNguoiDung")
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name ="maNguPhap")
    private NguPhap nguPhap;

    @ManyToOne
    @JoinColumn(name ="maHanTu")
    private HanTu hanTu;

    @ManyToOne
    @JoinColumn(name ="maBoThe")
    private BoTheGhiNho boTheGhiNho;

    @ManyToOne
    @JoinColumn(name ="maTuVung")
    private TuVung tuVung;

    @ManyToOne
    @JoinColumn(name = "maMauCau")
    private MauCau mauCau;
}
