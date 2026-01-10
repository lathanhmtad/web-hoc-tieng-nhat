package com.example.Oboe.Repository;

import com.example.Oboe.Entity.YEU_THICH;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
    public interface FavoritesRepository extends JpaRepository<YEU_THICH, UUID> {
    @Query("SELECT f FROM YEU_THICH f WHERE f.nguoiDung.maNguoiDung = :userId")
    List<YEU_THICH> findByUserId(@Param("userId") UUID userId);


    @Query("SELECT f FROM YEU_THICH f WHERE f.nguoiDung.maNguoiDung = :userId AND f.hanTu.maHanTu = :kanjiId")
    Optional<YEU_THICH> findFavoriteKanji(@Param("userId") UUID userId, @Param("kanjiId") UUID kanjiId);


    @Query("SELECT f FROM YEU_THICH f WHERE f.nguoiDung.maNguoiDung = :userId AND f.nguPhap.maNguPhap = :grammaid")
    Optional<YEU_THICH> findFavoriteGramma(@Param("userId") UUID userId, @Param("grammaid") UUID grammaId);

    @Query("SELECT f FROM YEU_THICH f WHERE f.nguoiDung.maNguoiDung = :userId AND f.tuVung.maTuVung = :vocabId")
    Optional<YEU_THICH> findFavoriteVocabulary(@Param("userId") UUID userId, @Param("vocabId") UUID vocabId);


    @Query("SELECT f FROM YEU_THICH f WHERE f.nguoiDung.maNguoiDung = :userId AND f.mauCau.maMauCau = :sentenceId")
    Optional<YEU_THICH> findFavoriteSentence(@Param("userId") UUID userId, @Param("sentenceId") UUID sentenceId);


}
