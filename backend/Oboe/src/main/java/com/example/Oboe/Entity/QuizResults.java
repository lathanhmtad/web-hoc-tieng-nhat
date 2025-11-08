package com.example.Oboe.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "QuizResults")
public class QuizResults {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ResultID", updatable = false, nullable = false)
    private UUID resultID;

    private double score;
    private LocalDateTime takenAt;

    @ManyToOne
    @JoinColumn(name = "QuizzesID", nullable = false)
    private Quizzes quiz;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private NguoiDung nguoiDung;

    public UUID getResultID() {
        return resultID;
    }

    public void setResultID(UUID resultID) {
        this.resultID = resultID;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public LocalDateTime getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(LocalDateTime takenAt) {
        this.takenAt = takenAt;
    }

    public Quizzes getQuiz() {
        return quiz;
    }

    public void setQuiz(Quizzes quiz) {
        this.quiz = quiz;
    }

    public NguoiDung getUser() {
        return nguoiDung;
    }

    public void setUser(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }
}
