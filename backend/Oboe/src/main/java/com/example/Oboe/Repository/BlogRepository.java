package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.TopicPostProjection;
import com.example.Oboe.Entity.BAI_VIET;
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
public interface BlogRepository extends JpaRepository<BAI_VIET, UUID> {

    // Method tìm kiếm theo tieuDe
    List<BAI_VIET> findByTieuDeContainingIgnoreCase(String keyword);
    List<BAI_VIET> findByTheContainingIgnoreCase(String keyword);
    List<BAI_VIET> findByChuDeContainingIgnoreCase(String keyword);

    @Query("SELECT b FROM BAI_VIET b WHERE " +
            "LOWER(b.tieuDe) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.the) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.chuDe) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<BAI_VIET> searchByKeyword(@Param("keyword") String keyword);


    // Method lấy tất cả blog của user
    @Query("SELECT b FROM BAI_VIET b WHERE b.nguoiDung.maNguoiDung = :userId")
    List<BAI_VIET> findBlogsByUserId(@Param("userId") UUID userId);

    @Query("SELECT b FROM BAI_VIET b WHERE b.nguoiDung.maNguoiDung = :userId")
    Page<BAI_VIET> findBlogsByUserIds(@Param("userId") UUID userId, Pageable pageabl);

    // lấy chủ đề nổi bật sử dụng interface TopicPostProjection
    @Query(value = """
    SELECT
        BIN_TO_UUID(b.ma_bai_viet) AS blogId,
        b.tieu_de AS title,
        COUNT(c.ma_binh_luan) AS commentCount
    FROM bai_viet b
    JOIN binh_luan c ON b.ma_bai_viet = c.ma_tham_chieu
    GROUP BY b.ma_bai_viet, b.tieu_de
    ORDER BY commentCount DESC
    LIMIT 3
    """, nativeQuery = true)
    List<TopicPostProjection> findTop3BlogsByCommentCount();

    @Query("SELECT COUNT(b) FROM BAI_VIET b WHERE b.nguoiDung.maNguoiDung = :userId")
    long countBlogsByUserId(@Param("userId") UUID userId);
    @Modifying
    @Transactional

    @Query("DELETE FROM BAI_VIET b WHERE b.nguoiDung.maNguoiDung = :userId")
    void deleteBlogsbyUser(@Param("userId") UUID userId);

    // Tổng số blog
    @Query("SELECT COUNT(b) FROM BAI_VIET b")
    Long countAllPosts();

    // Số blog trong tháng hiện tại
    @Query("SELECT COUNT(b) FROM BAI_VIET b WHERE FUNCTION('MONTH', b.ngayTao) = FUNCTION('MONTH', CURRENT_DATE)")
    Long countPostsThisMonth();



}