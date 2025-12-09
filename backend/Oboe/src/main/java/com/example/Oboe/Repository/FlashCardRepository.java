package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.FlashcardSearchResultDTO;
import com.example.Oboe.Entity.BoTheGhiNho;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlashCardRepository extends JpaRepository<BoTheGhiNho, UUID> {

    @Query("SELECT f FROM BoTheGhiNho f WHERE f.nguoiDung.maNguoiDung = :userId")
    List<BoTheGhiNho> findByUser_maNguoiDung(@Param("userId") UUID userId);

    @Query("SELECT f FROM BoTheGhiNho f WHERE f.nguoiDung.maNguoiDung = :userId AND LOWER(f.tenBoThe) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<BoTheGhiNho> searchByUserIdAndTerm(@Param("userId") UUID userId, @Param("term") String term);

    @Query("SELECT f FROM BoTheGhiNho f WHERE f.nguoiDung.maNguoiDung = :userId")
    Page<BoTheGhiNho> findByUser(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT f FROM BoTheGhiNho f WHERE f.nguoiDung.maNguoiDung = :userId")
    List<BoTheGhiNho> findflashcardByUserId(@Param("userId") UUID userId);


    @Query("SELECT f FROM BoTheGhiNho f WHERE f.nguoiDung.maNguoiDung = :userId AND LOWER(f.tenBoThe) LIKE LOWER(CONCAT('%', :term, '%'))")
    Page<BoTheGhiNho> searchByUserIdAndTerm(@Param("userId") UUID userId,
                                            @Param("term") String term,
                                            Pageable pageable);
    @Query("SELECT COUNT(f) FROM BoTheGhiNho f WHERE f.nguoiDung.maNguoiDung = :userId")
    long countFlashCardByUserId(@Param("userId") UUID userId);

    @Query("SELECT f FROM BoTheGhiNho f WHERE f.nguoiDung.maNguoiDung = :userId ORDER BY f.ngayTao DESC")
    List<BoTheGhiNho> findTop5ByUserIdOrderByCreatedDesc(@Param("userId") UUID userId);

    @Query("DELETE FROM BoTheGhiNho f WHERE f.nguoiDung.maNguoiDung = :userId")
    void deleteUser(@Param("userId") UUID userId);


    @Query("SELECT f FROM BoTheGhiNho f WHERE f.maBoThe = :setId")
    Optional<BoTheGhiNho> findById(@Param("setId") UUID setId);

    @Query(
            value = "SELECT new com.example.Oboe.DTOs.FlashcardSearchResultDTO(" +
                    "f.maBoThe, f.tenBoThe, f.nguoiDung.email, COUNT(c), f.nguoiDung.anhDaiDien) " +
                    "FROM BoTheGhiNho f LEFT JOIN f.chiTietThes c " +
                    "WHERE LOWER(f.tenBoThe) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                    "GROUP BY f.maBoThe, f.moTa, f.nguoiDung.email, f.nguoiDung.anhDaiDien",
            countQuery = "SELECT COUNT(DISTINCT f.maBoThe) " +
                    "FROM BoTheGhiNho f " +
                    "WHERE LOWER(f.tenBoThe) LIKE LOWER(CONCAT('%', :keyword, '%'))"
    )
    Page<FlashcardSearchResultDTO> searchFlashcardsByKeyword(@Param("keyword") String keyword, Pageable pageable);



}