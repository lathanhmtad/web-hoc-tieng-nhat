package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "cau_tra_loi_nguoi_dung")
public class CauTraLoiNguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_cau_tra_loi", updatable = false, nullable = false)
    private UUID maCauTraLoi;

    private String cauTraLoi;
    private boolean laChinhXac;

    @ManyToOne
    @JoinColumn(name = "maCauHoi", nullable = false)
    private CauHoi cauHoi;

    @ManyToOne
    @JoinColumn(name = "maNguoiDung", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "QuizzesID", nullable = false)
    private BaiKiemTra quiz;

    public int getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(int attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    @Column(name = "attempt_number")
    private int attemptNumber;
}
