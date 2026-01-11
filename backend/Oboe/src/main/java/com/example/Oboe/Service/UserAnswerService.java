package com.example.Oboe.Service;

import com.example.Oboe.DTOs.QuizResultDTO;
import com.example.Oboe.DTOs.UserAnswerDTO;
import com.example.Oboe.Entity.*;
import com.example.Oboe.Repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserAnswerService {
    private UserAnswerRepository userAnswerRepository;
    private QuestionsRepository questionsRepository;
    private QuizzesRepository quizzesRepository;
    private UserRepository userRepository;
    private QuizResultRepository quizResultRepository;

    @Transactional
    public QuizResultDTO saveUserAnswer(List<UserAnswerDTO> answers, UUID userId, UUID quizzesId) {
        // Lấy QuizID
        BAI_KIEM_TRA quiz = quizzesRepository.findById(quizzesId).orElse(null);
        if (quiz == null) {
            return new QuizResultDTO("Không tìm thấy Quiz!", 0, 0, 0, LocalDateTime.now());
        }
        //Lấy Userid
        NGUOI_DUNG nguoiDung = userRepository.findById(userId).orElse(null);
        if (nguoiDung == null) {
            return new QuizResultDTO("Không tìm thấy User!", 0, 0, 0, LocalDateTime.now());
        }

        // Đếm tổng số câu hỏi
        List<CAU_HOI> allQuestions = questionsRepository.findByBaiKiemTra_MaBaiKiemTra(quizzesId);
        int totalQuestions = allQuestions.size();

        if (totalQuestions == 0) {
            return new QuizResultDTO("Quiz không có câu hỏi nào!", 0, totalQuestions, answers.size(), LocalDateTime.now());
        }
        if (answers.size() < totalQuestions) {
            return new QuizResultDTO("Bạn chưa trả lời hết tất cả các câu hỏi trong quiz này!", 0, totalQuestions, answers.size(), LocalDateTime.now());
        }

        if (answers.size() > totalQuestions) {
            return new QuizResultDTO("Bạn đã trả lời nhiều hơn số câu hỏi trong quiz này!", 0, totalQuestions, answers.size(), LocalDateTime.now());
        }

        // 3. Convert List câu hỏi sang Map để tìm kiếm nhanh (O(1)) theo ID
        Map<UUID, CAU_HOI> questionMap = allQuestions.stream()
                .collect(Collectors.toMap(CAU_HOI::getMaCauHoi, Function.identity())); // Giả sử getter ID là getMaCauHoi()

        long correctAnswers = 0;
        List<CHI_TIET_BAI_LAM> chiTietBaiLamList = new ArrayList<>();

        for (UserAnswerDTO dto : answers) {
            CAU_HOI question = questionMap.get(dto.getQuestionId());

            if (question == null) {
                // Tùy chọn: throw exception hoặc continue. Ở đây mình continue để tránh crash
                continue;
            }
            // Kiểm tra đúng/sai
            boolean isCorrect = dto.getAnswer().equalsIgnoreCase(question.getDapAnChinhXac());

            CHI_TIET_BAI_LAM chiTietBaiLam = new CHI_TIET_BAI_LAM();
            chiTietBaiLam.setCauHoi(question);
            chiTietBaiLam.setNoiDungTraLoi(dto.getAnswer());
            chiTietBaiLam.setLaDapAnDung(isCorrect);
            chiTietBaiLamList.add(chiTietBaiLam);

            if (isCorrect) correctAnswers++;
        }

        double score = ((double) correctAnswers / totalQuestions) * 100;
        score = Math.round(score * 100.0) / 100.0;
        LocalDateTime takenAt = LocalDateTime.now();

        try {
            LICH_SU_LAM_BAI result = new LICH_SU_LAM_BAI();
            result.setBaiKiemTra(quiz);
            result.setNguoiDung(nguoiDung);
            result.setDiemSo(score);
            result.setThoiGianLamBai(takenAt);

            LICH_SU_LAM_BAI savedResult = quizResultRepository.save(result);

            for (CHI_TIET_BAI_LAM item : chiTietBaiLamList) {
                item.setLichSuLamBai(savedResult);
            }

            userAnswerRepository.saveAll(chiTietBaiLamList);
            return new QuizResultDTO("Lưu đáp án thành công!", score, totalQuestions, correctAnswers, takenAt);
        } catch (Exception e) {
            e.printStackTrace(); // hoặc log lỗi
            return new QuizResultDTO("Lỗi khi lưu kết quả quiz!", 0, totalQuestions, correctAnswers, LocalDateTime.now());
        }
    }
}





