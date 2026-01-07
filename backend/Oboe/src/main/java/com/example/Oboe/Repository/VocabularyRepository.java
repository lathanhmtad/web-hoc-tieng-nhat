package com.example.Oboe.Repository;

import com.example.Oboe.Entity.TuVung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VocabularyRepository extends JpaRepository<TuVung, UUID> {
    @Query("SELECT v FROM TuVung v WHERE LOWER(v.noiDungTu) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(v.nghia) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(v.phatAmTiengViet) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<TuVung> searchVocabulary(@Param("keyword") String keyword);

    // Nếu cần tìm từ theo KanjiId
}