package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "PHAN_HOI")
public class PHAN_HOI {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID maPhanHoi;

    @Column(nullable = false)
    private String hoVaTen;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String tieuDe;

    private String chuDe;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    private LocalDateTime ngayTao = LocalDateTime.now();
}
