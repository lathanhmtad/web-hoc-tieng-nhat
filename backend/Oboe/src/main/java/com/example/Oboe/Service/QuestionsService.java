package com.example.Oboe.Service;

import com.example.Oboe.DTOs.QuestionDTO;
import com.example.Oboe.Entity.CauHoi;
import com.example.Oboe.Entity.BaiKiemTra;
import com.example.Oboe.Repository.QuestionsRepository;
import com.example.Oboe.Repository.QuizzesRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class QuestionsService {

    private QuestionsRepository questionsRepository;

    private QuizzesRepository quizzesRepository;


    // đùng List để tạo được nhiều câu hỏi hơn thay vì gọi mỗi QuestionDTO tạo dc từng câu hỏi
    public List<QuestionDTO> create(List<QuestionDTO> dtoList) {

        List<QuestionDTO> createdQuestions = new ArrayList<>();

        for (QuestionDTO dto : dtoList) {
            BaiKiemTra quiz = quizzesRepository.findById(dto.getQuizId())
                    .orElseThrow(() -> new RuntimeException("Quiz not found with ID: " + dto.getQuizId()));

            CauHoi question = new CauHoi();
            question.setTenCauHoi(dto.getQuestionName());
            question.setDapAnChinhXac(dto.getCorrectAnswer());
            question.setLuuChon(String.join(";", dto.getOptions()));
            question.setBaiKiemTra(quiz);

            CauHoi saved = questionsRepository.save(question);
            createdQuestions.add(toDTO(saved));
        }

        return createdQuestions;
    }

    public Page<QuestionDTO> getQuestionsByQuizId(UUID quizId, Pageable pageable) {
        BaiKiemTra quiz = quizzesRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        return questionsRepository.findByBaiKiemTra(quiz, pageable)
                .map(this::toDTO); // chuyển Page<Questions> -> Page<QuestionDTO>
    }



    private QuestionDTO toDTO(CauHoi q) {
        QuestionDTO dto = new QuestionDTO();
        dto.setQuestionID(q.getMaCauHoi());
        dto.setQuestionName(q.getTenCauHoi());
        dto.setCorrectAnswer(q.getDapAnChinhXac());

        String optionsStr = q.getLuuChon().trim();

        if (optionsStr.startsWith("[") && optionsStr.endsWith("]")) {
            // Trường hợp bị lưu kiểu JSON string
            optionsStr = optionsStr.replaceAll("^\\[|\\]$", "")  // Bỏ [ và ]
                    .replaceAll("\"", "");        // Bỏ dấu "
            dto.setOptions(Arrays.asList(optionsStr.split(",")));
        } else {
            // Trường hợp bình thường: cách nhau bằng dấu ;
            dto.setOptions(Arrays.asList(optionsStr.split(";")));
        }

        dto.setQuizId(q.getBaiKiemTra().getMaBaiKiemTra());
        return dto;
    }

}
