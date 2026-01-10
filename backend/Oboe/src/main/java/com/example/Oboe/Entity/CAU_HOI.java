package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "CAU_HOI")
@Getter
@Setter
public class CAU_HOI {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maCauHoi;

    private String tenCauHoi;
    private String dapAnChinhXac;
    private String luaChon;

    @ManyToOne
    @JoinColumn(name = "maBaiKiemTra")
    private BAI_KIEM_TRA baiKiemTra;
}