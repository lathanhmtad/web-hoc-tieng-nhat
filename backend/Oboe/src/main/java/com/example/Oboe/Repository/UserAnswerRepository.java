package com.example.Oboe.Repository;

import com.example.Oboe.Entity.CauTraLoiNguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserAnswerRepository extends JpaRepository<CauTraLoiNguoiDung, UUID> {

    @Query("SELECT MAX(u.attemptNumber) FROM CauTraLoiNguoiDung u WHERE u.nguoiDung.maNguoiDung = :userId AND u.quiz.maBaiKiemTra = :quizId")
    Integer findMaxAttemptNumber(@Param("userId") UUID userId, @Param("quizId") UUID quizId);
}
