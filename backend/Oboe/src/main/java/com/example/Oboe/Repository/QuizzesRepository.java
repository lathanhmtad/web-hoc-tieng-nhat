package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.QuizSearchResultDTO;
import com.example.Oboe.Entity.BAI_KIEM_TRA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizzesRepository extends JpaRepository<BAI_KIEM_TRA, UUID> {
    @Query("SELECT COUNT(q) FROM BAI_KIEM_TRA q WHERE q.nguoiDung.maNguoiDung = :userId")
    long countQuizzesByUserId(@Param("userId") UUID userId);

    @Query("SELECT b FROM BAI_KIEM_TRA b WHERE b.nguoiDung.maNguoiDung = :userId")
    List<BAI_KIEM_TRA> findQuizzesByUserId(UUID userId);

    //phân trang
    @Query("SELECT b FROM BAI_KIEM_TRA b WHERE b.nguoiDung.maNguoiDung = :userId")
    Page<BAI_KIEM_TRA> findQuizzesByUserIds(@Param("userId") UUID userId, Pageable pageable);


    @Query(value = "SELECT * FROM BAI_KIEM_TRA ORDER BY RAND() LIMIT 3", nativeQuery = true)
    List<BAI_KIEM_TRA> findRandomQuizzes();

    @Query("SELECT new com.example.Oboe.DTOs.QuizSearchResultDTO(q.maBaiKiemTra, q.tieuDe, SIZE(q.cauHoiList), q.nguoiDung.email, q.nguoiDung.anhDaiDien) " +
            "FROM BAI_KIEM_TRA q WHERE LOWER(q.tieuDe) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<QuizSearchResultDTO> searchQuizzesByKeyword(@Param("keyword") String keyword,Pageable pageable);

    Optional<BAI_KIEM_TRA> findById(UUID id);

}