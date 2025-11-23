package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "muc_the")
public class MucThe {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @Column(name = "CardItemId", updatable = false, nullable = false)
        private UUID cardItemId;

        private String word;
        private String meaning;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "maBoThe", nullable = false)
        @JsonBackReference("set-carditems")
        private BoThe boThe;

        public MucThe() {}
        public MucThe(String word, String meaning) {
            this.word = word;
            this.meaning = meaning;
        }

        public UUID getCardItemId() {
            return cardItemId;
        }

        public void setCardItemId(UUID cardItemId) {
            this.cardItemId = cardItemId;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public String getMeaning() {
            return meaning;
        }

        public void setMeaning(String meaning) {
            this.meaning = meaning;
        }

        public BoThe getBoThe() {
            return boThe;
        }

        public void setBoThe(BoThe boThe) {
            this.boThe = boThe;
        }
}
