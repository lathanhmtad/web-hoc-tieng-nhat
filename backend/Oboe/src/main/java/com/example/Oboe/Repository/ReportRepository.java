package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.BlogReportDTO;
import com.example.Oboe.Entity.BAO_CAO;
import com.example.Oboe.Entity.TRANG_THAI_BAO_CAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<BAO_CAO, UUID> {
    @Query("SELECT r FROM BAO_CAO r WHERE r.nguoiDung.maNguoiDung = :userId")
    List<BAO_CAO> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT r FROM BAO_CAO r WHERE r.baiViet.maBaiViet = :blogId")
    List<BAO_CAO> findByBlogId(@Param("blogId") UUID blogId);

    @Query("SELECT COUNT(r) FROM BAO_CAO r WHERE r.trangThai = 'PENDING'")
    Long countPendingReports();

    @Query("SELECT r FROM BAO_CAO r ORDER BY r.ngayBaoCao DESC")
    List<LocalDate> getLatestReportTime();

    @Query("SELECT r FROM BAO_CAO r ORDER BY r.ngayBaoCao DESC")
    List<BAO_CAO> findLatestReport();

    @Query("SELECT COUNT(r) FROM BAO_CAO r WHERE r.baiViet IS NOT NULL AND r.trangThai = 'PENDING'")
    Long countPendingBlogReports();

    @Query("SELECT COUNT(r) FROM BAO_CAO r WHERE r.baiViet IS NULL AND r.trangThai = 'PENDING'")
    Long countPendingFeedbackReports();

    @Query("""
                SELECT new com.example.Oboe.DTOs.BlogReportDTO(
                    r.maBaoCao,
                    b.tieuDe,
                    b.noiDung,
                    u.email,
                    u.phuongThucXacThuc,
                    u.anhDaiDien,
                    r.tieuDe,
                    r.noiDung,
                    r.trangThai,
                    r.ngayBaoCao,
                    b.maBaiViet,
                    (SELECT COUNT(r2) FROM BAO_CAO r2 WHERE r2.nguoiDung.maNguoiDung = u.maNguoiDung)
                )
                FROM BAO_CAO r
                JOIN r.baiViet b
                JOIN r.nguoiDung u
                WHERE (:title IS NULL OR LOWER(b.tieuDe) LIKE LOWER(CONCAT('%', :title, '%')))
                  AND (:type IS NULL OR LOWER(r.tieuDe) LIKE LOWER(CONCAT('%', :type, '%')))
                  AND (:status IS NULL OR r.trangThai = :status)
                ORDER BY r.ngayBaoCao DESC
            """)
    List<BlogReportDTO> searchBlogReports(
            @Param("title") String title,
            @Param("type") String type,
            @Param("status") TRANG_THAI_BAO_CAO status
    );


    @Query("""
                SELECT new com.example.Oboe.DTOs.BlogReportDTO(
                    r.maBaoCao,
                    b.tieuDe,
                    b.noiDung,
                    u.email,
                    u.phuongThucXacThuc,
                    u.anhDaiDien,
                    r.tieuDe,
                    r.noiDung,
                    r.trangThai,
                    r.ngayBaoCao,
                    b.maBaiViet,
                    (SELECT COUNT(r2) FROM BAO_CAO r2 WHERE r2.nguoiDung.maNguoiDung = u.maNguoiDung)
                )
                FROM BAO_CAO r
                JOIN r.baiViet b
                JOIN r.nguoiDung u
                ORDER BY r.ngayBaoCao DESC
            """)
    List<BlogReportDTO> findAllBlogReports();

    @Modifying
    @Query("UPDATE BAO_CAO r SET r.trangThai = :status WHERE r.maBaoCao = :reportId")
    void updateReportStatus(@Param("reportId") UUID reportId, @Param("status") TRANG_THAI_BAO_CAO status);
}
