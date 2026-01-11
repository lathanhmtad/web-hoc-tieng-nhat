package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "NGU_PHAP")
public class NGU_PHAP {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maNguPhap;
    private String cauTruc;
    private String giaiThich;
    private String viDu;
    private String loaiNguPhap;
    private String trinhDoJlpt;

    @OneToMany(mappedBy = "nguPhap")
    private List<MAU_CAU> mauCaus;
}