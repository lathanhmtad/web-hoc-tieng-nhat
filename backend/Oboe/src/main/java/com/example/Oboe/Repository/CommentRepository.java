package com.example.Oboe.Repository;

import com.example.Oboe.Entity.BINH_LUAN;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<BINH_LUAN, UUID> {
    List<BINH_LUAN> findByMaThamChieu(UUID referenceId);
    Long countByMaThamChieu(UUID referenceId);
    @Query("SELECT b FROM BINH_LUAN b WHERE b.nguoiDung.maNguoiDung = :userId")
    List<BINH_LUAN> findCommentByUserId(UUID userId);

    @Query("SELECT b FROM BINH_LUAN b WHERE b.nguoiDung.maNguoiDung = :userId")
    Page<BINH_LUAN> findCommentByUserIds(UUID userId, Pageable pageabl);
    //Để thêm người comment gần nhất cho một bài blog
    Optional<BINH_LUAN> findTopByMaThamChieuOrderByNgayTaoDesc(UUID referenceId);

    @Modifying
    @Transactional
    @Query("DELETE FROM BINH_LUAN c WHERE c.nguoiDung.maNguoiDung = :userId")
    void deleteUserbyComment(@Param("userId") UUID userId);

    @Query("SELECT COUNT(c) FROM BINH_LUAN c WHERE c.nguoiDung.maNguoiDung = :userId")
    long countCommentsByUserId(@Param("userId") UUID userId);

}
