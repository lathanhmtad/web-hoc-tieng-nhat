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
@Table(name="bo_the_ghi_nho")
public class BoTheGhiNho {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_bo_the", updatable = false, nullable = false)
    private UUID maBoThe;

    private String tenBoThe; // Tên bộ thẻ
    private String moTa; // Mô tả về bộ thẻ

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    @JsonBackReference("user-flashcards")
    private NguoiDung nguoiDung;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime ngayTao;

    @OneToMany(mappedBy = "boTheGhiNho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietThe> chiTietThes = new ArrayList<>();
}
