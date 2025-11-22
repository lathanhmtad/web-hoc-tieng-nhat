package com.example.Oboe.Entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "Favorites")
public class Favorites {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "FavoritesID", updatable = false, nullable = false)
    private UUID FavoritesID;
    private String title;
    private String content;
    private LocalDate favories_at = LocalDate.now();



    @ManyToOne
    @JoinColumn(name = "user_id")
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name ="grammaID")
    private NguPhap gramma;

    @ManyToOne
    @JoinColumn(name ="kanjiId")
    private HanTu hanTu;

    @ManyToOne
    @JoinColumn(name ="CardId")
    private BoThe boThe;

    @ManyToOne
    @JoinColumn(name ="Vocalb_id")
    private TuVung tuVung;

    @ManyToOne
    @JoinColumn(name = "sample_sentence_id")
    private MauCau sentence;

    public MauCau getSentence() {
        return sentence;
    }

    public void setSentence(MauCau sentence) {
        this.sentence = sentence;
    }



    //Contructor
    public Favorites() {

    }

    public BoThe getFlashCards() {
        return boThe;
    }

    public void setBoThe(BoThe boThe) {
        this.boThe = boThe;
    }

    public UUID getFavoritesID() {
        return FavoritesID;
    }

    public void setFavoritesID(UUID favoritesID) {
        FavoritesID = favoritesID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getFavories_at() {
        return favories_at;
    }

    public void setFavories_at(LocalDate favories_at) {
        this.favories_at = favories_at;
    }

    public NguoiDung getUser() {
        return nguoiDung;
    }

    public void setUser(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    public NguPhap getGramma() {
        return gramma;
    }

    public void setGramma(NguPhap gramma) {
        this.gramma = gramma;
    }

    public HanTu getHanTu() {
        return hanTu;
    }

    public void setHanTu(HanTu hanTu) {
        this.hanTu = hanTu;
    }

    public TuVung getTuVung() {
        return tuVung;
    }

    public void setTuVung(TuVung tuVung) {
        this.tuVung = tuVung;
    }




}
