package com.example.Oboe.Entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "cau_hoi")
public class CauHoi {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "QuestionID", updatable = false, nullable = false)
    private UUID questionID;

    private String questionName;
    private String correctAnswer;
    private String options;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QuizzesID")
    private BaiKiemTra quiz;

    public UUID getQuestionID() {
        return questionID;
    }

    public void setQuestionID(UUID questionID) {
        this.questionID = questionID;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public BaiKiemTra getQuiz() {
        return quiz;
    }

    public void setQuiz(BaiKiemTra quiz) {
        this.quiz = quiz;
    }
}
