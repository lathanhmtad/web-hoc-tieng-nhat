package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "xu_ly_bao_cao")
public class XuLyBaoCao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_xu_ly", columnDefinition = "BINARY(16)")
    private UUID maXuLy;

    @ManyToOne
    @JoinColumn(name = "maBaoCao", nullable = false)
    private BaoCao baoCao;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_xu_ly", nullable = false, length = 50)
    private LoaiXuLy loaiXuLy;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private LocalDateTime ngayTao;
}
