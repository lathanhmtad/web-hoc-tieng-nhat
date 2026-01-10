package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "TIN_NHAN")
public class TIN_NHAN {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID maTinNhan;

    private String noiDung;

    private LocalDateTime thoiGianGui = LocalDateTime.now();

    // Message relationships
    @ManyToOne
    @JoinColumn(name = "ma_nguoi_gui")
    @JsonBackReference("user-sent-messages")
    private NGUOI_DUNG nguoiGui;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_nhan")
    @JsonBackReference("user-received-messages")
    private NGUOI_DUNG nguoiNhan;

    @PreUpdate
    public void preUpdate() {
        this.thoiGianGui = LocalDateTime.now();
    }

    public UUID getMaNguoiGui() {
        return nguoiGui != null ? nguoiGui.getMaNguoiDung() : null;
    }

    public UUID getMaNguoiNhan() {
        return nguoiNhan != null ? nguoiNhan.getMaNguoiDung() : null;
    }
}
