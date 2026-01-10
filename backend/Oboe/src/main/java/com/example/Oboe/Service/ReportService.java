package com.example.Oboe.Service;

import com.example.Oboe.DTOs.BlogReportDTO;
import com.example.Oboe.DTOs.ReportDtos;
import com.example.Oboe.Entity.*;
import com.example.Oboe.Repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ReportService {

    private UserRepository userRepository;

    private ReportRepository reportRepository;

    private ReportActionsRepository reportActionsRepository;

    private BlogRepository blogRepository;

    private NotificationsRepository notificationsRepository;

    // Tạo báo cáo
    public BAO_CAO createReport(ReportDtos reportDtos, UUID userId) {
        NGUOI_DUNG nguoiDung = userRepository.findById(userId).orElse(null);
        if (nguoiDung == null) return null;

        BAO_CAO baoCao = new BAO_CAO();
        baoCao.setTieuDe(reportDtos.getTitle());
        baoCao.setNoiDung(reportDtos.getContent());
        baoCao.setNguoiDung(nguoiDung);
        baoCao.setNgayBaoCao(LocalDate.now());
        baoCao.setTrangThai(TRANG_THAI_BAO_CAO.PENDING);

        // Nếu có blogId thì đây là report blog
        if (reportDtos.getBlogId() != null) {
            BAI_VIET baiViet = blogRepository.findById(reportDtos.getBlogId()).orElse(null);
            if (baiViet != null) {
                baoCao.setBaiViet(baiViet);
            }
        }

        return reportRepository.save(baoCao);
    }

    public List<BAO_CAO> getAllReports() {
        return reportRepository.findAll();
    }

    public boolean updateStatus(UUID reportId, TRANG_THAI_BAO_CAO status) {
        BAO_CAO baoCao = reportRepository.findById(reportId).orElse(null);
        if (baoCao == null) return false;
        baoCao.setTrangThai(status);
        reportRepository.save(baoCao);
        return true;
    }

    public List<BAO_CAO> getReportsByBlogId(UUID blogId) {
        return reportRepository.findByBlogId(blogId);
    }

    public List<BAO_CAO> getReportsByUserId(UUID userId) {
        return reportRepository.findByUserId(userId);
    }

    public boolean deleteReport(UUID reportId) {
        if (!reportRepository.existsById(reportId)) return false;
        reportRepository.deleteById(reportId);
        return true;
    }

    public List<BlogReportDTO> searchBlogReports(String title, String type, TRANG_THAI_BAO_CAO status) {
        return reportRepository.searchBlogReports(title, type, status);
    }

    public List<BlogReportDTO> getAllBlogReports() {
        return reportRepository.findAllBlogReports();
    }

    @Transactional
    public void approveReport(UUID reportId, LOAI_XU_LY loaiXuLy, String note) {

        BAO_CAO baoCao = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));


        baoCao.setTrangThai(TRANG_THAI_BAO_CAO.APPROVED);
        reportRepository.save(baoCao);


        XU_LY_BAO_CAO action = new XU_LY_BAO_CAO();
        action.setBaoCao(baoCao);
        action.setLoaiXuLy(loaiXuLy);
        action.setGhiChu(note);
        reportActionsRepository.save(action);

        switch (loaiXuLy) {
            case WARNING:
                if (baoCao.getNguoiDung() != null) {
                    NGUOI_DUNG nguoiDung = baoCao.getNguoiDung();
                    BAI_VIET baiViet = baoCao.getBaiViet();
                    THONG_BAO notification = new THONG_BAO();
                    notification.setNguoiDung(nguoiDung);
                    notification.setNoiDung("Bạn đã nhận cảnh cáo từ admin: " + note);
                    notification.setDaDuocDoc(false);
                    notification.setMaDoiTuong(baoCao.getMaBaoCao());
                    notification.setLoaiDoiTuong("REPORT");

                    notificationsRepository.save(notification);
                }
                break;
            case DELETE_POST:
                if (baoCao.getBaiViet() != null) {
                    BAI_VIET baiViet = baoCao.getBaiViet();
                    blogRepository.delete(baiViet);
                }
                break;
            case BAN_USER:
                if (baoCao.getNguoiDung() != null) {
                    NGUOI_DUNG nguoiDung = baoCao.getNguoiDung();
                    nguoiDung.setTrangThaiTaiKhoan(TRANG_THAI_TAI_KHOAN.BAN);
                    userRepository.save(nguoiDung);
                }
                break;
        }
    }

    @Transactional
    public void rejectedReport(UUID reportId) {
        reportRepository.updateReportStatus(reportId, TRANG_THAI_BAO_CAO.REJECTED);
    }
}
