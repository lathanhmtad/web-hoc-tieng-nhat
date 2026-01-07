package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.BlogReportDTO;
import com.example.Oboe.Entity.BaoCao;
import com.example.Oboe.Entity.TrangThaiBaoCao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<BaoCao, UUID> {
    @Query("SELECT r FROM BaoCao r WHERE r.nguoiDung.maNguoiDung = :userId")
    List<BaoCao> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT r FROM BaoCao r WHERE r.baiViet.maBaiViet = :blogId")
    List<BaoCao> findByBlogId(@Param("blogId") UUID blogId);

    @Query("SELECT COUNT(r) FROM BaoCao r WHERE r.trangThai = 'PENDING'")
    Long countPendingReports();

    @Query("SELECT r FROM BaoCao r ORDER BY r.ngayBaoCao DESC")
    List<LocalDate> getLatestReportTime();

    @Query("SELECT r FROM BaoCao r ORDER BY r.ngayBaoCao DESC")
    List<BaoCao> findLatestReport();

    @Query("SELECT COUNT(r) FROM BaoCao r WHERE r.baiViet IS NOT NULL AND r.trangThai = 'PENDING'")
    Long countPendingBlogReports();

    @Query("SELECT COUNT(r) FROM BaoCao r WHERE r.baiViet IS NULL AND r.trangThai = 'PENDING'")
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
                    (SELECT COUNT(r2) FROM BaoCao r2 WHERE r2.nguoiDung.maNguoiDung = u.maNguoiDung)
                )
                FROM BaoCao r
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
            @Param("status") TrangThaiBaoCao status
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
                    (SELECT COUNT(r2) FROM BaoCao r2 WHERE r2.nguoiDung.maNguoiDung = u.maNguoiDung)
                )
                FROM BaoCao r
                JOIN r.baiViet b
                JOIN r.nguoiDung u
                ORDER BY r.ngayBaoCao DESC
            """)
    List<BlogReportDTO> findAllBlogReports();

    @Modifying
    @Query("UPDATE BaoCao r SET r.trangThai = :status WHERE r.maBaoCao = :reportId")
    void updateReportStatus(@Param("reportId") UUID reportId, @Param("status") TrangThaiBaoCao status);
}
