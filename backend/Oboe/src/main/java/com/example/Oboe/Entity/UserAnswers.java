package com.example.Oboe.Entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "UserAnswers")
public class UserAnswers {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "UserAnswersId", updatable = false, nullable = false)
    private UUID userAnswersId;

    private String answer;
    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "QuestionID", nullable = false)
    private CauHoi question;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
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

    // Getters and Setters
    public UUID getUserAnswersId() {
        return userAnswersId;
    }

    public void setUserAnswersId(UUID userAnswersId) {
        this.userAnswersId = userAnswersId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public CauHoi getQuestion() {
        return question;
    }

    public void setQuestion(CauHoi question) {
        this.question = question;
    }

    public NguoiDung getUser() {
        return nguoiDung;
    }

    public void setUser(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    public BaiKiemTra getQuiz() {
        return quiz;
    }

    public void setQuiz(BaiKiemTra quiz) {
        this.quiz = quiz;
    }
}
