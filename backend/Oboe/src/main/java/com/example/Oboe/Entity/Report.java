package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "Reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "reportID", updatable = false, nullable = false)
    private UUID reportID;

    private String title;
    private String content;

    private LocalDate report_at = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = true) // Cho phép null nếu không report blog
    @JsonBackReference
    private BaiViet baiViet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    // Getters & Setters
    public UUID getReportID() {
        return reportID;
    }

    public void setReportID(UUID reportID) {
        this.reportID = reportID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getReport_at() {
        return report_at;
    }

    public void setReport_at(LocalDate report_at) {
        this.report_at = report_at;
    }

    public NguoiDung getUser() {
        return nguoiDung;
    }

    public void setUser(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public BaiViet getBlog() {
        return baiViet;
    }

    public void setBlog(BaiViet baiViet) {
        this.baiViet = baiViet;
    }
}
