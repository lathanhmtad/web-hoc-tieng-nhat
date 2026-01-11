package com.example.Oboe.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "MAU_CAU")
public class MAU_CAU {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column( updatable = false, nullable = false)
    private UUID maMauCau;
    private String cauTiengNhat;
    private String nghiaTiengViet;

    @ManyToOne
    @JoinColumn(name = "maNguPhap")
    private NGU_PHAP nguPhap;
}
