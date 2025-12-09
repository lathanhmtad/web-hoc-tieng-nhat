package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "chi_tiet_the")
public class ChiTietThe {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @Column(name = "ma_the", updatable = false, nullable = false)
        private UUID maThe;

        private String tuVung;
        private String nghia;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "ma_bo_the", nullable = false)
        @JsonBackReference("set-carditems")
        private BoTheGhiNho boTheGhiNho;

        public ChiTietThe() {}

        public ChiTietThe(String tuVung, String nghia) {
            this.tuVung = tuVung;
            this.nghia = nghia;
        }
}
