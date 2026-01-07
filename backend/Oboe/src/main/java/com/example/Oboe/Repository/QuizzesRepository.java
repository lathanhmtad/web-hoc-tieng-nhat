package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.QuizSearchResultDTO;
import com.example.Oboe.Entity.BaiKiemTra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizzesRepository extends JpaRepository<BaiKiemTra, UUID> {
    @Query("SELECT COUNT(q) FROM BaiKiemTra q WHERE q.nguoiDung.maNguoiDung = :userId")
    long countQuizzesByUserId(@Param("userId") UUID userId);

    @Query("SELECT b FROM BaiKiemTra b WHERE b.nguoiDung.maNguoiDung = :userId")
    List<BaiKiemTra> findQuizzesByUserId(UUID userId);

    //phân trang
    @Query("SELECT b FROM BaiKiemTra b WHERE b.nguoiDung.maNguoiDung = :userId")
    Page<BaiKiemTra> findQuizzesByUserIds(@Param("userId") UUID userId, Pageable pageable);


    @Query(value = "SELECT * FROM bai_kiem_tra ORDER BY RAND() LIMIT 3", nativeQuery = true)
    List<BaiKiemTra> findRandomQuizzes();

    @Query("SELECT new com.example.Oboe.DTOs.QuizSearchResultDTO(q.maBaiKiemTra, q.tieuDe, SIZE(q.cauHoiList), q.nguoiDung.email, q.nguoiDung.anhDaiDien) " +
            "FROM BaiKiemTra q WHERE LOWER(q.tieuDe) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<QuizSearchResultDTO> searchQuizzesByKeyword(@Param("keyword") String keyword,Pageable pageable);

    Optional<BaiKiemTra> findById(UUID id);

}