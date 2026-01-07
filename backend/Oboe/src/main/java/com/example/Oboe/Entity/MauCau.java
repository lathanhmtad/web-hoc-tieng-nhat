package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "mau_cau")
public class MauCau {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_mau_cau", updatable = false, nullable = false)
    private UUID maMauCau;
    private String cauTiengNhat;
    private String nghiaTiengViet;
    @ManyToOne
    @JoinColumn(name = "ma_ngu_phap")
    private NguPhap nguPhap;
}
