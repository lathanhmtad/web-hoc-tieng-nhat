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
@Table(name = "han_tu")
public class HanTu {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.BINARY)
    @Column(name = "ma_han_tu", updatable = false, nullable = false)
    private UUID maHanTu;

    private String kyTu;

    private String nghia;

    private String soNet;

    private String phatAmTiengViet;

    private String onYomi;
    private String kunYomi;

    @Column(name = "trinh_do_jlpt")
    private String trinhDoJLPT;

    @OneToMany(mappedBy = "hanTu")
    private List<ChiTietTuVung> cacTuVungLienQuan;
}
