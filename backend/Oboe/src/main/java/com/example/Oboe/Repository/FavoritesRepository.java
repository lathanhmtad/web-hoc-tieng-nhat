package com.example.Oboe.Repository;

import com.example.Oboe.Entity.YeuThich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
    public interface FavoritesRepository extends JpaRepository<YeuThich, UUID> {
    @Query("SELECT f FROM YeuThich f WHERE f.nguoiDung.maNguoiDung = :userId")
    List<YeuThich> findByUserId(@Param("userId") UUID userId);


    @Query("SELECT f FROM YeuThich f WHERE f.nguoiDung.maNguoiDung = :userId AND f.hanTu.maHanTu = :kanjiId")
    Optional<YeuThich> findFavoriteKanji(@Param("userId") UUID userId, @Param("kanjiId") UUID kanjiId);


    @Query("SELECT f FROM YeuThich f WHERE f.nguoiDung.maNguoiDung = :userId AND f.nguPhap.maNguPhap = :grammaid")
    Optional<YeuThich> findFavoriteGramma(@Param("userId") UUID userId, @Param("grammaid") UUID grammaId);

    @Query("SELECT f FROM YeuThich f WHERE f.nguoiDung.maNguoiDung = :userId AND f.tuVung.maTuVung = :vocabId")
    Optional<YeuThich> findFavoriteVocabulary(@Param("userId") UUID userId, @Param("vocabId") UUID vocabId);


    @Query("SELECT f FROM YeuThich f WHERE f.nguoiDung.maNguoiDung = :userId AND f.mauCau.maMauCau = :sentenceId")
    Optional<YeuThich> findFavoriteSentence(@Param("userId") UUID userId, @Param("sentenceId") UUID sentenceId);


}
