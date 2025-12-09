package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;


import java.sql.Types;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "han_tu")
public class HanTu {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.BINARY)
    @Column(name = "kanji_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID kanjiId;

    @Column(name = "character_name", nullable = false)
    private String character_name;

    @Column(name = "meaning")
    private String meaning;

    @Column(name = "strokes")
    private String strokes;

    @Column(name = "vietnamese_pronunciation")
    private String vietnamesePronunciation;

    @OneToMany(mappedBy = "hanTu")
    private List<ChiTietTuVung> cacTuVungLienQuan;
}
