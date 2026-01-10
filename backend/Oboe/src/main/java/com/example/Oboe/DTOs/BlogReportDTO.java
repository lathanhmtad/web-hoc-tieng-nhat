package com.example.Oboe.DTOs;

import com.example.Oboe.Entity.PHUONG_THUC_XAC_THUC;
import com.example.Oboe.Entity.TRANG_THAI_BAO_CAO;

import java.time.LocalDate;
import java.util.UUID;

public class BlogReportDTO {
    private UUID reportId;
    private String blogTitle;
    private String blogContent;
    private String userName;
    private PHUONG_THUC_XAC_THUC phuongThucXacThuc;
    private String avatarUrl;
    private String type; // report tieuDe
    private String content; // report content
    private TRANG_THAI_BAO_CAO status;
    private LocalDate  report_at;
    private UUID blogId;
    private long reportCount;

    public BlogReportDTO() {}
    public BlogReportDTO(UUID reportId, String blogTitle, String blogContent, String userName,
                         PHUONG_THUC_XAC_THUC phuongThucXacThuc, String avatarUrl, String type,
                         String content, TRANG_THAI_BAO_CAO status, LocalDate report_at, UUID blogId, long reportCount) {
        this.reportId = reportId;
        this.blogTitle = blogTitle;
        this.blogContent = blogContent;
        this.userName = userName;
        this.phuongThucXacThuc = phuongThucXacThuc;
        this.avatarUrl = avatarUrl;
        this.type = type;
        this.content = content;
        this.status = status;
        this.report_at = report_at;
        this.blogId = blogId;
        this.reportCount = reportCount;

    }

    public UUID getBlogId() {
        return blogId;
    }

    public void setBlogId(UUID blogId) {
        this.blogId = blogId;
    }

    public long getReportCount() {
        return reportCount;
    }

    public void setReportCount(long reportCount) {
        this.reportCount = reportCount;
    }

    public UUID getReportId() {
        return reportId;
    }

    public void setReportId(UUID reportId) {
        this.reportId = reportId;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getBlogContent() {
        return blogContent;
    }

    public void setBlogContent(String blogContent) {
        this.blogContent = blogContent;
    }

    public String getEmail() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public PHUONG_THUC_XAC_THUC getAuthProvider() {
        return phuongThucXacThuc;
    }

    public void setAuthProvider(PHUONG_THUC_XAC_THUC phuongThucXacThuc) {
        this.phuongThucXacThuc = phuongThucXacThuc;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TRANG_THAI_BAO_CAO getStatus() {
        return status;
    }

    public void setStatus(TRANG_THAI_BAO_CAO status) {
        this.status = status;
    }

    public LocalDate getReport_at() {
        return report_at;
    }

    public void setReport_at(LocalDate report_at) {
        this.report_at = report_at;
    }
}
