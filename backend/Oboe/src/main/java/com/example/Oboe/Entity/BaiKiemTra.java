package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "bai_kiem_tra")
public class BaiKiemTra {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_bai_kiem_tra", updatable = false, nullable = false)
    private UUID maBaiKiemTra;

    private String tieuDe;
    private String moTa;

    @OneToMany(mappedBy = "baiKiemTra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CauHoi> cauHoiList;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maNguoiDung") // Liên kết với User
    private NguoiDung nguoiDung;

}
