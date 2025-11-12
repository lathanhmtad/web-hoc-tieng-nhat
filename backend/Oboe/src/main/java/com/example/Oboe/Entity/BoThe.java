package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="bo_the")
public class BoThe {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "set_id", updatable = false, nullable = false)
    private UUID set_id;

    private String term; // Tên bộ thẻ
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-flashcards")
    private NguoiDung nguoiDung;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created;

    @OneToMany(mappedBy = "boThe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MucThe> mucThes = new ArrayList<>();

    public BoThe() {}
    public BoThe(String term, String description, NguoiDung nguoiDung) {
        this.term = term;
        this.description = description;
        this.nguoiDung = nguoiDung;
    }

    public UUID getSet_id() {
        return set_id;
    }

    public void setSet_id(UUID set_id) {
        this.set_id = set_id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public NguoiDung getUser() {
        return nguoiDung;
    }

    public void setUser(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public List<MucThe> getMucThes() {
        return mucThes;
    }

    public void setMucThes(List<MucThe> mucThes) {
        this.mucThes = mucThes;
    }
}
