package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "CHI_TIET_THE")
public class CHI_TIET_THE {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @Column(updatable = false, nullable = false)
        private UUID maThe;

        private String tuVung;
        private String nghia;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "maBoThe", nullable = false)
        @JsonBackReference("set-carditems")
        private BO_THE_GHI_NHO boTheGhiNho;

        public CHI_TIET_THE() {}

        public CHI_TIET_THE(String tuVung, String nghia) {
            this.tuVung = tuVung;
            this.nghia = nghia;
        }
}
