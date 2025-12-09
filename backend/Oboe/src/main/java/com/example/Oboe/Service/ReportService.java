package com.example.Oboe.Service;

import com.example.Oboe.DTOs.BlogReportDTO;
import com.example.Oboe.DTOs.ReportDtos;
import com.example.Oboe.Entity.*;
import com.example.Oboe.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ReportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportActionsRepository reportActionsRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private final NotificationsRepository notificationsRepository;

    public ReportService(NotificationsRepository notificationsRepository) {
        this.notificationsRepository = notificationsRepository;
    }

    // Tạo báo cáo
    public Report createReport(ReportDtos reportDtos, UUID userId) {
        NguoiDung nguoiDung = userRepository.findById(userId).orElse(null);
        if (nguoiDung == null) return null;

        Report report = new Report();
        report.setTitle(reportDtos.getTitle());
        report.setContent(reportDtos.getContent());
        report.setUser(nguoiDung);
        report.setReport_at(LocalDate.now());
        report.setStatus(ReportStatus.PENDING);

        // Nếu có blogId thì đây là report blog
        if (reportDtos.getBlogId() != null) {
            BaiViet baiViet = blogRepository.findById(reportDtos.getBlogId()).orElse(null);
            if (baiViet != null) {
                report.setBlog(baiViet);
            }
        }

        return reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public boolean updateStatus(UUID reportId, ReportStatus status) {
        Report report = reportRepository.findById(reportId).orElse(null);
        if (report == null) return false;
        report.setStatus(status);
        reportRepository.save(report);
        return true;
    }

    public List<Report> getReportsByBlogId(UUID blogId) {
        return reportRepository.findByBlogId(blogId);
    }

    public List<Report> getReportsByUserId(UUID userId) {
        return reportRepository.findByUserId(userId);
    }

    public boolean deleteReport(UUID reportId) {
        if (!reportRepository.existsById(reportId)) return false;
        reportRepository.deleteById(reportId);
        return true;
    }

    public List<BlogReportDTO> searchBlogReports(String title, String type, ReportStatus status) {
        return reportRepository.searchBlogReports(title, type, status);
    }

    public List<BlogReportDTO> getAllBlogReports() {
        return reportRepository.findAllBlogReports();
    }

    @Transactional
    public void approveReport(UUID reportId, ActionType actionType, String note) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));


        report.setStatus(ReportStatus.APPROVED);
        reportRepository.save(report);


        ReportActions action = new ReportActions();
        action.setReport(report);
        action.setActionType(actionType);
        action.setNote(note);
        reportActionsRepository.save(action);

        switch (actionType) {
            case WARNING:
                if (report.getUser() != null) {
                    NguoiDung nguoiDung = report.getUser();
                    BaiViet baiViet = report.getBlog();
                    ThongBao notification = new ThongBao();
                    notification.setNguoiDung(nguoiDung);
                    notification.setText_notification("Bạn đã nhận cảnh cáo từ admin: " + note);
                    notification.setRead(false);
                    notification.setTargetId(report.getReportID());
                    notification.setTargetType("REPORT");

                    notificationsRepository.save(notification);
                }
                break;
            case DELETE_POST:
                if (report.getBlog() != null) {
                    BaiViet baiViet = report.getBlog();
                    blogRepository.delete(baiViet);
                }
                break;
            case BAN_USER:
                if (report.getUser() != null) {
                    NguoiDung nguoiDung = report.getUser();
                    nguoiDung.setTrangThaiTaiKhoan(TrangThaiTaiKhoan.BAN);
                    userRepository.save(nguoiDung);
                }
                break;
        }
    }

    @Transactional
    public void rejectedReport(UUID reportId) {
        reportRepository.updateReportStatus(reportId, ReportStatus.REJECTED);
    }
}
