package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;


import java.sql.Types;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "HAN_TU")
public class HAN_TU {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.BINARY)
    @Column(updatable = false, nullable = false)
    private UUID maHanTu;

    private String kyTu;

    private String nghia;

    private Integer soNet;

    private String phatAmTiengViet;

    private String onYomi;
    private String kunYomi;

    private String trinhDoJlpt;

    @OneToMany(mappedBy = "hanTu")
    private List<CHI_TIET_TU_VUNG> cacTuVungLienQuan;
}
