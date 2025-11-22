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
    @Column(name = "Vocalb_id", updatable = false, nullable = false)

    private UUID vocalbId;

    private String words;

    private String meanning;

    private String wordType; //  lưu loại từ: noun, verb, adj...

    private String scriptType; //  hiragana, katakana

    @Column(name = "vietnamese_pronunciation")
    private String vietnamesePronunciation;

    @ManyToOne
    @JoinColumn(name ="kanjiId")
    private HanTu hanTu;

    @OneToMany(mappedBy = "tuVung")
    private List<TuVungHanTu> chiTietHanTu;
}