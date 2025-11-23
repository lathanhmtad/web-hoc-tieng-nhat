package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.FlashcardSearchResultDTO;
import com.example.Oboe.Entity.BoThe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlashCardRepository extends JpaRepository<BoThe, UUID> {

    @Query("SELECT f FROM BoThe f WHERE f.nguoiDung.user_id = :userId")
    List<BoThe> findByUser_User_id(@Param("userId") UUID userId);

    @Query("SELECT f FROM BoThe f WHERE f.nguoiDung.user_id = :userId AND LOWER(f.tenBoThe) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<BoThe> searchByUserIdAndTerm(@Param("userId") UUID userId, @Param("term") String term);

    @Query("SELECT f FROM BoThe f WHERE f.nguoiDung.user_id = :userId")
    Page<BoThe> findByUser(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT f FROM BoThe f WHERE f.nguoiDung.user_id = :userId")
    List<BoThe> findflashcardByUserId(@Param("userId") UUID userId);


    @Query("SELECT f FROM BoThe f WHERE f.nguoiDung.user_id = :userId AND LOWER(f.tenBoThe) LIKE LOWER(CONCAT('%', :term, '%'))")
    Page<BoThe> searchByUserIdAndTerm(@Param("userId") UUID userId,
                                      @Param("term") String term,
                                      Pageable pageable);
    @Query("SELECT COUNT(f) FROM BoThe f WHERE f.nguoiDung.user_id = :userId")
    long countFlashCardByUserId(@Param("userId") UUID userId);

    @Query("SELECT f FROM BoThe f WHERE f.nguoiDung.user_id = :userId ORDER BY f.ngayTao DESC")
    List<BoThe> findTop5ByUserIdOrderByCreatedDesc(@Param("userId") UUID userId);

    @Query("DELETE FROM BoThe f WHERE f.nguoiDung.user_id = :userId")
    void deleteUser(@Param("userId") UUID userId);


    @Query("SELECT f FROM BoThe f WHERE f.maBoThe = :setId")
    Optional<BoThe> findById(@Param("setId") UUID setId);

    @Query(
            value = "SELECT new com.example.Oboe.DTOs.FlashcardSearchResultDTO(" +
                    "f.maBoThe, f.tenBoThe, f.nguoiDung.userName, COUNT(c), f.nguoiDung.avatarUrl) " +
                    "FROM BoThe f LEFT JOIN f.mucThes c " +
                    "WHERE LOWER(f.tenBoThe) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                    "GROUP BY f.maBoThe, f.moTa, f.nguoiDung.userName, f.nguoiDung.avatarUrl",
            countQuery = "SELECT COUNT(DISTINCT f.maBoThe) " +
                    "FROM BoThe f " +
                    "WHERE LOWER(f.tenBoThe) LIKE LOWER(CONCAT('%', :keyword, '%'))"
    )
    Page<FlashcardSearchResultDTO> searchFlashcardsByKeyword(@Param("keyword") String keyword, Pageable pageable);



}