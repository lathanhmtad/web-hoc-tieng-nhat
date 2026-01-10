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
@Table(name = "BAO_CAO")
public class BAO_CAO {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maBaoCao;

    private String tieuDe;
    private String noiDung;

    private LocalDate ngayBaoCao = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "maNguoiDung")
    @JsonBackReference
    private NGUOI_DUNG nguoiDung;

    @ManyToOne
    @JoinColumn(name = "maBaiViet", nullable = true) // Cho phép null nếu không report blog
    @JsonBackReference
    private BAI_VIET baiViet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TRANG_THAI_BAO_CAO trangThai = TRANG_THAI_BAO_CAO.PENDING;
}
