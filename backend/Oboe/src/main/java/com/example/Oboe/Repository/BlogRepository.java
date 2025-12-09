package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.TopicPostProjection;
import com.example.Oboe.Entity.BaiViet;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BlogRepository extends JpaRepository<BaiViet, UUID> {

    // Method tìm kiếm theo title
    List<BaiViet> findByTitleContainingIgnoreCase(String keyword);
    List<BaiViet> findByTagsContainingIgnoreCase(String keyword);
    List<BaiViet> findByTopicsContainingIgnoreCase(String keyword);

    @Query("SELECT b FROM BaiViet b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.tags) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.topics) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<BaiViet> searchByKeyword(@Param("keyword") String keyword);


    // Method lấy tất cả blog của user
    @Query("SELECT b FROM BaiViet b WHERE b.nguoiDung.maNguoiDung = :userId")
    List<BaiViet> findBlogsByUserId(@Param("userId") UUID userId);

    @Query("SELECT b FROM BaiViet b WHERE b.nguoiDung.maNguoiDung = :userId")
    Page<BaiViet> findBlogsByUserIds(@Param("userId") UUID userId, Pageable pageabl);

    // lấy chủ đề nổi bật sử dụng interface TopicPostProjection
    @Query(value = """
    SELECT 
        BIN_TO_UUID(b.blog_id) AS blogId,
        b.title AS title,
        COUNT(c.comment_id) AS commentCount
    FROM bai_viet b
    JOIN binhLuans c ON b.blog_id = c.reference_id
    GROUP BY b.blog_id, b.title
    ORDER BY commentCount DESC
    LIMIT 3
    """, nativeQuery = true)
    List<TopicPostProjection> findTop3BlogsByCommentCount();




    @Query("SELECT COUNT(b) FROM BaiViet b WHERE b.nguoiDung.maNguoiDung = :userId")
    long countBlogsByUserId(@Param("userId") UUID userId);
    @Modifying
    @Transactional

    @Query("DELETE FROM BaiViet b WHERE b.nguoiDung.maNguoiDung = :userId")
    void deleteBlogsbyUser(@Param("userId") UUID userId);

    // Tổng số blog
    @Query("SELECT COUNT(b) FROM BaiViet b")
    Long countAllPosts();

    // Số blog trong tháng hiện tại
    @Query("SELECT COUNT(b) FROM BaiViet b WHERE FUNCTION('MONTH', b.createdAt) = FUNCTION('MONTH', CURRENT_DATE)")
    Long countPostsThisMonth();



}