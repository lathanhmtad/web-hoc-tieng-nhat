package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "CACH_DOC")
public class CACH_DOC {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maCachDoc;

    @Column(name = "cach_doc_thuc_te", nullable = false)
    private String cachDocThucTe;  // Cách đọc thực tế

    @Column(name = "loai_doc", nullable = false)
    private String loaiDoc;  // Loại đọc: onyomi, kunyomi, nanori, hiragana, katakana, grammar,...

    @Column(name = "loai_doi_tuong", nullable = false)
    private String loaiDoiTuong;    // Bảng cha: "kanji", "vocabulary", "gramma"

    @Column(name = "ma_doi_tuong", nullable = false)
    private UUID maDoiTuong;        // ID từ bảng cha (UUID của Kanji, Vocabulary hoặc Gramma)

}
