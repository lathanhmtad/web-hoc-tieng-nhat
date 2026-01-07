package com.example.Oboe.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "tu_vung")
public class TuVung {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_tu_vung", updatable = false, nullable = false)
    private UUID maTuVung;

    private String noiDungTu;
    private String nghia;
    private String loaiTu; //  lưu loại từ: noun, verb, adj...
    private String kieuChu; //  hiragana, katakana
    private String phatAmTiengViet;

    @OneToMany(mappedBy = "tuVung")
    private List<ChiTietTuVung> chiTietTuVungs;
}