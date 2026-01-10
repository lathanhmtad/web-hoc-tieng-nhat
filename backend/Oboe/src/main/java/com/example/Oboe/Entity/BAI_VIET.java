package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "BAI_VIET")
public class BAI_VIET {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maBaiViet;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    @Column(nullable = false)
    private String tieuDe;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String noiDung;

    @Column(nullable = false, updatable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime ngayCapNhat = LocalDateTime.now();

    @Column(name = "the")
    private String the; // Ví dụ: "java,spring,backend"

    @Column(name = "chuDe")
    private String chuDe; // Ví dụ: "Spring Boot,Cơ sở dữ liệu"

    // Mối quan hệ nhiều-một với User
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maNguoiDung", nullable = false)
    @JsonBackReference("user-blogs") // Tương ứng với @JsonManagedReference trong User
    private NGUOI_DUNG nguoiDung;

    @OneToMany(mappedBy = "baiViet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BAO_CAO> baoCaos = new ArrayList<>();

    @PreUpdate
    public void preUpdate() {
        this.ngayCapNhat = LocalDateTime.now();
    }
}