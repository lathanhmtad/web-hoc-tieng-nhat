package com.example.Oboe.Repository;

import com.example.Oboe.Entity.HanTu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface KanjiRepository extends JpaRepository<HanTu, UUID> {
    @Query("SELECT k FROM HanTu k WHERE LOWER(k.character_name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(k.meaning) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(k.vietnamesePronunciation) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<HanTu> searchKanji(@Param("keyword") String keyword);


    @Query("SELECT k FROM HanTu k WHERE LOWER(k.meaning) LIKE LOWER(CONCAT('%', :meaning, '%')) AND k.kanjiId <> :kanjiId")
    List<HanTu> findRelatedByMeaning(@Param("meaning") String meaning, @Param("kanjiId") UUID kanjiId);

}
