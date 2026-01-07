package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "ngu_phap")
public class NguPhap {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_ngu_phap", updatable = false, nullable = false)
    private UUID maNguPhap;
    private String cauTruc;
    private String giaiThich;
    private String viDu;
    private String loaiNguPhap;

    @Column(name = "trinh_do_jlpt")
    private String trinhDoJLPT;
}