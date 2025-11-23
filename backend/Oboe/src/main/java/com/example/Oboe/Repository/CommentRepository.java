package com.example.Oboe.Repository;

import com.example.Oboe.Entity.BinhLuan;
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

public interface CommentRepository extends JpaRepository<BinhLuan, UUID> {
    List<BinhLuan> findByReferenceId(UUID referenceId);
    Long countByReferenceId(UUID referenceId);
    @Query("SELECT b FROM BinhLuan b WHERE b.nguoiDung.user_id = :userId")
    List<BinhLuan> findCommentByUserId(UUID userId);

    @Query("SELECT b FROM BinhLuan b WHERE b.nguoiDung.user_id = :userId")
    Page<BinhLuan> findCommentByUserIds(UUID userId, Pageable pageabl);
    //Để thêm người comment gần nhất cho một bài blog
    Optional<BinhLuan> findTopByReferenceIdOrderByCreatedAtDesc(UUID referenceId);

    @Modifying
    @Transactional
    @Query("DELETE FROM BinhLuan c WHERE c.nguoiDung.user_id = :userId")
    void deleteUserbyComment(@Param("userId") UUID userId);

    @Query("SELECT COUNT(c) FROM BinhLuan c WHERE c.nguoiDung.user_id = :userId")
    long countCommentsByUserId(@Param("userId") UUID userId);

}
