package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name="BO_THE_GHI_NHO")
public class BO_THE_GHI_NHO {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maBoThe;

    private String tenBoThe; // Tên bộ thẻ
    private String moTa; // Mô tả về bộ thẻ

    @ManyToOne
    @JoinColumn(name = "maNguoiDung", nullable = false)
    @JsonBackReference("user-flashcards")
    private NGUOI_DUNG nguoiDung;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime ngayTao;

    @OneToMany(mappedBy = "boTheGhiNho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CHI_TIET_THE> chiTietThes = new ArrayList<>();
}
