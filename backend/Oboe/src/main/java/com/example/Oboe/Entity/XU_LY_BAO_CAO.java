package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "XU_LY_BAO_CAO")
public class XU_LY_BAO_CAO {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID maXuLy;

    @ManyToOne
    @JoinColumn(name = "maBaoCao", nullable = false)
    private BAO_CAO baoCao;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_xu_ly", nullable = false, length = 50)
    private LOAI_XU_LY loaiXuLy;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private LocalDateTime ngayTao;
}
