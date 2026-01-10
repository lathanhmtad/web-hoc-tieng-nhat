package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.FlashcardSearchResultDTO;
import com.example.Oboe.Entity.BO_THE_GHI_NHO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlashCardRepository extends JpaRepository<BO_THE_GHI_NHO, UUID> {

    @Query("SELECT f FROM BO_THE_GHI_NHO f WHERE f.nguoiDung.maNguoiDung = :userId")
    List<BO_THE_GHI_NHO> findByUser_maNguoiDung(@Param("userId") UUID userId);

    @Query("SELECT f FROM BO_THE_GHI_NHO f WHERE f.nguoiDung.maNguoiDung = :userId AND LOWER(f.tenBoThe) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<BO_THE_GHI_NHO> searchByUserIdAndTerm(@Param("userId") UUID userId, @Param("term") String term);

    @Query("SELECT f FROM BO_THE_GHI_NHO f WHERE f.nguoiDung.maNguoiDung = :userId")
    Page<BO_THE_GHI_NHO> findByUser(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT f FROM BO_THE_GHI_NHO f WHERE f.nguoiDung.maNguoiDung = :userId")
    List<BO_THE_GHI_NHO> findflashcardByUserId(@Param("userId") UUID userId);

    @Query("SELECT f FROM BO_THE_GHI_NHO f WHERE f.nguoiDung.maNguoiDung = :userId AND LOWER(f.tenBoThe) LIKE LOWER(CONCAT('%', :term, '%'))")
    Page<BO_THE_GHI_NHO> searchByUserIdAndTerm(@Param("userId") UUID userId,
                                               @Param("term") String term,
                                               Pageable pageable);
    @Query("SELECT COUNT(f) FROM BO_THE_GHI_NHO f WHERE f.nguoiDung.maNguoiDung = :userId")
    long countFlashCardByUserId(@Param("userId") UUID userId);

    @Query("SELECT f FROM BO_THE_GHI_NHO f WHERE f.nguoiDung.maNguoiDung = :userId ORDER BY f.ngayTao DESC")
    List<BO_THE_GHI_NHO> findTop5ByUserIdOrderByCreatedDesc(@Param("userId") UUID userId);

    @Query("DELETE FROM BO_THE_GHI_NHO f WHERE f.nguoiDung.maNguoiDung = :userId")
    void deleteUser(@Param("userId") UUID userId);

    @Query("SELECT f FROM BO_THE_GHI_NHO f WHERE f.maBoThe = :setId")
    Optional<BO_THE_GHI_NHO> findById(@Param("setId") UUID setId);

    @Query(
            value = "SELECT new com.example.Oboe.DTOs.FlashcardSearchResultDTO(" +
                    "f.maBoThe, f.tenBoThe, f.nguoiDung.email, COUNT(c), f.nguoiDung.anhDaiDien) " +
                    "FROM BO_THE_GHI_NHO f LEFT JOIN f.chiTietThes c " +
                    "WHERE LOWER(f.tenBoThe) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                    "GROUP BY f.maBoThe, f.moTa, f.nguoiDung.email, f.nguoiDung.anhDaiDien",
            countQuery = "SELECT COUNT(DISTINCT f.maBoThe) " +
                    "FROM BO_THE_GHI_NHO f " +
                    "WHERE LOWER(f.tenBoThe) LIKE LOWER(CONCAT('%', :keyword, '%'))"
    )
    Page<FlashcardSearchResultDTO> searchFlashcardsByKeyword(@Param("keyword") String keyword, Pageable pageable);



}