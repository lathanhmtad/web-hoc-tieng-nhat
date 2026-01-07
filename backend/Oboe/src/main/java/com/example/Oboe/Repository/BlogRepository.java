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

    // Method tìm kiếm theo tieuDe
    List<BaiViet> findByTieuDeContainingIgnoreCase(String keyword);
    List<BaiViet> findByTheContainingIgnoreCase(String keyword);
    List<BaiViet> findByChuDeContainingIgnoreCase(String keyword);

    @Query("SELECT b FROM BaiViet b WHERE " +
            "LOWER(b.tieuDe) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.the) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.chuDe) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<BaiViet> searchByKeyword(@Param("keyword") String keyword);


    // Method lấy tất cả blog của user
    @Query("SELECT b FROM BaiViet b WHERE b.nguoiDung.maNguoiDung = :userId")
    List<BaiViet> findBlogsByUserId(@Param("userId") UUID userId);

    @Query("SELECT b FROM BaiViet b WHERE b.nguoiDung.maNguoiDung = :userId")
    Page<BaiViet> findBlogsByUserIds(@Param("userId") UUID userId, Pageable pageabl);

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
    @Query("SELECT COUNT(b) FROM BaiViet b WHERE FUNCTION('MONTH', b.ngayTao) = FUNCTION('MONTH', CURRENT_DATE)")
    Long countPostsThisMonth();



}