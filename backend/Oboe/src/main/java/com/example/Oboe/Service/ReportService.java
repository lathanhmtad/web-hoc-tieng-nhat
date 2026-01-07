package com.example.Oboe.Service;

import com.example.Oboe.DTOs.BlogReportDTO;
import com.example.Oboe.DTOs.ReportDtos;
import com.example.Oboe.Entity.*;
import com.example.Oboe.Repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    public BaoCao createReport(ReportDtos reportDtos, UUID userId) {
        NguoiDung nguoiDung = userRepository.findById(userId).orElse(null);
        if (nguoiDung == null) return null;

        BaoCao baoCao = new BaoCao();
        baoCao.setTieuDe(reportDtos.getTitle());
        baoCao.setNoiDung(reportDtos.getContent());
        baoCao.setNguoiDung(nguoiDung);
        baoCao.setNgayBaoCao(LocalDate.now());
        baoCao.setTrangThai(TrangThaiBaoCao.PENDING);

        // Nếu có blogId thì đây là report blog
        if (reportDtos.getBlogId() != null) {
            BaiViet baiViet = blogRepository.findById(reportDtos.getBlogId()).orElse(null);
            if (baiViet != null) {
                baoCao.setBaiViet(baiViet);
            }
        }

        return reportRepository.save(baoCao);
    }

    public List<BaoCao> getAllReports() {
        return reportRepository.findAll();
    }

    public boolean updateStatus(UUID reportId, TrangThaiBaoCao status) {
        BaoCao baoCao = reportRepository.findById(reportId).orElse(null);
        if (baoCao == null) return false;
        baoCao.setTrangThai(status);
        reportRepository.save(baoCao);
        return true;
    }

    public List<BaoCao> getReportsByBlogId(UUID blogId) {
        return reportRepository.findByBlogId(blogId);
    }

    public List<BaoCao> getReportsByUserId(UUID userId) {
        return reportRepository.findByUserId(userId);
    }

    public boolean deleteReport(UUID reportId) {
        if (!reportRepository.existsById(reportId)) return false;
        reportRepository.deleteById(reportId);
        return true;
    }

    public List<BlogReportDTO> searchBlogReports(String title, String type, TrangThaiBaoCao status) {
        return reportRepository.searchBlogReports(title, type, status);
    }

    public List<BlogReportDTO> getAllBlogReports() {
        return reportRepository.findAllBlogReports();
    }

    @Transactional
    public void approveReport(UUID reportId, LoaiXuLy loaiXuLy, String note) {

        BaoCao baoCao = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));


        baoCao.setTrangThai(TrangThaiBaoCao.APPROVED);
        reportRepository.save(baoCao);


        XuLyBaoCao action = new XuLyBaoCao();
        action.setBaoCao(baoCao);
        action.setLoaiXuLy(loaiXuLy);
        action.setGhiChu(note);
        reportActionsRepository.save(action);

        switch (loaiXuLy) {
            case WARNING:
                if (baoCao.getNguoiDung() != null) {
                    NguoiDung nguoiDung = baoCao.getNguoiDung();
                    BaiViet baiViet = baoCao.getBaiViet();
                    ThongBao notification = new ThongBao();
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
                    BaiViet baiViet = baoCao.getBaiViet();
                    blogRepository.delete(baiViet);
                }
                break;
            case BAN_USER:
                if (baoCao.getNguoiDung() != null) {
                    NguoiDung nguoiDung = baoCao.getNguoiDung();
                    nguoiDung.setTrangThaiTaiKhoan(TrangThaiTaiKhoan.BAN);
                    userRepository.save(nguoiDung);
                }
                break;
        }
    }

    @Transactional
    public void rejectedReport(UUID reportId) {
        reportRepository.updateReportStatus(reportId, TrangThaiBaoCao.REJECTED);
    }
}
