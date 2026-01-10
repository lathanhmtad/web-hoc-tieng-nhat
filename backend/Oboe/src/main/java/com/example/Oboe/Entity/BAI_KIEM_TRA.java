package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "`BAI_KIEM_TRA`")
public class BAI_KIEM_TRA {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maBaiKiemTra;

    private String tieuDe;
    private String moTa;

    @OneToMany(mappedBy = "baiKiemTra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CAU_HOI> cauHoiList;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maNguoiDung") // Liên kết với User
    private NGUOI_DUNG nguoiDung;

}
