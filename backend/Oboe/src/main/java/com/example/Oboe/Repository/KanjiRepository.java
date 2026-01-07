package com.example.Oboe.Repository;

import com.example.Oboe.Entity.HanTu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface KanjiRepository extends JpaRepository<HanTu, UUID> {
    @Query("SELECT k FROM HanTu k WHERE LOWER(k.kyTu) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(k.nghia) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(k.phatAmTiengViet) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<HanTu> searchKanji(@Param("keyword") String keyword);

    @Query("SELECT k FROM HanTu k WHERE LOWER(k.kyTu) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(k.nghia) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(k.phatAmTiengViet) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<HanTu> getAll(@Param("keyword") String keyword, Pageable pageable);


    @Query("SELECT k FROM HanTu k WHERE LOWER(k.nghia) LIKE LOWER(CONCAT('%', :meaning, '%')) AND k.maHanTu <> :kanjiId")
    List<HanTu> findRelatedByMeaning(@Param("meaning") String meaning, @Param("kanjiId") UUID kanjiId);

}
