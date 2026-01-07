package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "bao_cao")
public class BaoCao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_bao_cao", updatable = false, nullable = false)
    private UUID maBaoCao;

    private String tieuDe;
    private String noiDung;

    private LocalDate ngayBaoCao = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "maNguoiDung")
    @JsonBackReference
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "maBaiViet", nullable = true) // Cho phép null nếu không report blog
    @JsonBackReference
    private BaiViet baiViet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiBaoCao trangThai = TrangThaiBaoCao.PENDING;
}
