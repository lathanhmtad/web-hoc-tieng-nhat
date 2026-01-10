package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "AI_PHAN_HOI_BAI_VIET")
public class AI_PHAN_HOI_BAI_VIET {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID maPhanHoi;

    @ManyToOne
    @JoinColumn(name = "maBaiViet")
    private BAI_VIET baiViet;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String noiDung;

    @Column(name = "ngay_tao", columnDefinition = "DATETIME(6)")
    private LocalDateTime ngayTao;
}
