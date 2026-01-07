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
@Table(name = "tin_nhan")
public class TinNhan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_tin_nhan", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID maTinNhan;

    private String noiDung;

    private LocalDateTime thoiGianGui = LocalDateTime.now();

    // Message relationships
    @ManyToOne
    @JoinColumn(name = "ma_nguoi_gui")
    @JsonBackReference("user-sent-messages")
    private NguoiDung nguoiGui;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_nhan")
    @JsonBackReference("user-received-messages")
    private NguoiDung nguoiNhan;

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
