package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "binh_luan")
@Getter
@Setter
public class BinhLuan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_binh_luan", updatable = false, nullable = false)
    private UUID maBinhLuan;

    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    private String tieuDe;

    @NotBlank(message = "Nội dung không được để trống")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String noiDung;

    @Column(nullable = false, updatable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();

    // Nhiều - Một với User
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maNguoiDung", nullable = false)
    @JsonBackReference("user-comments")
    private NguoiDung nguoiDung;

    //  Gộp mục tiêu vào 1 trường referenceId
    @Column(name = "ma_tham_chieu", nullable = false)
    private UUID maThamChieu;

    // Quan hệ tự tham chiếu - comment cha
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_binh_luan_cha")
    @JsonBackReference("comment-parent")
    private BinhLuan binhLuanCha;

    // Danh sách comment con (replies)
    @OneToMany(mappedBy = "binhLuanCha", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("comment-parent")
    private List<BinhLuan> replies = new ArrayList<>();
}
