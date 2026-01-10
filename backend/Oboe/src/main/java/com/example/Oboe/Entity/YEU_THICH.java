package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "YEU_THICH")
public class YEU_THICH {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maYeuThich;
    private String tieuDe;
    private String noiDung;
    private LocalDate ngayTao = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "maNguoiDung")
    private NGUOI_DUNG nguoiDung;

    @ManyToOne
    @JoinColumn(name ="maNguPhap")
    private NGU_PHAP nguPhap;

    @ManyToOne
    @JoinColumn(name ="maHanTu")
    private HAN_TU hanTu;

    @ManyToOne
    @JoinColumn(name ="maBoThe")
    private BO_THE_GHI_NHO boTheGhiNho;

    @ManyToOne
    @JoinColumn(name ="maTuVung")
    private TU_VUNG tuVung;

    @ManyToOne
    @JoinColumn(name = "maMauCau")
    private MAU_CAU mauCau;
}
