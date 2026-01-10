package com.example.Oboe.Service;

import com.example.Oboe.DTOs.QuizDTO;
import com.example.Oboe.DTOs.QuizWithQuestionsDTO;
import com.example.Oboe.Entity.NGUOI_DUNG;
import com.example.Oboe.Entity.BAI_KIEM_TRA;
import com.example.Oboe.Repository.QuizzesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizzesService {

    @Autowired
    private QuizzesRepository quizzesRepository;


    private final UserService userService;


    public QuizzesService(UserService userService) {

        this.userService = userService;

    }

    public List<QuizDTO> getAll() {
        return quizzesRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    public List<QuizDTO> getAllByUserId(UUID userId) {
        Optional<NGUOI_DUNG> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return List.of(); // Trả về danh sách rỗng thay vì null
        List<BAI_KIEM_TRA> quizzes = quizzesRepository.findQuizzesByUserId(userId);
        return quizzes.stream()
                .map(this::toDTO)
                .toList();
    }
    public QuizDTO getById(UUID id) {
        return quizzesRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    public Page<QuizDTO> getQuizzesByUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BAI_KIEM_TRA> quizzesPage = quizzesRepository.findQuizzesByUserIds(userId, pageable);
        List<QuizDTO> dtoList = quizzesPage.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, quizzesPage.getTotalElements());
    }
    public QuizDTO create(QuizDTO dto , UUID userId ) {
        Optional<NGUOI_DUNG> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return null;
        BAI_KIEM_TRA quiz = new BAI_KIEM_TRA();
        quiz.setTieuDe(dto.getTitle());
        quiz.setMoTa(dto.getDescription());
        quiz.setNguoiDung(userOpt.get());
        return toDTO(quizzesRepository.save(quiz));
    }

    public QuizDTO update(UUID id, QuizDTO dto, UUID userId) {
        BAI_KIEM_TRA quiz = quizzesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        //  Kiểm tra quyền sở hữu
        if (!quiz.getNguoiDung().getMaNguoiDung().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this quiz");
        }
        quiz.setTieuDe(dto.getTitle());
        quiz.setMoTa(dto.getDescription());

        return toDTO(quizzesRepository.save(quiz));
    }


    public void delete(UUID id, UUID userId) {
        BAI_KIEM_TRA quiz = quizzesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        //  Kiểm tra quyền sở hữu
        if (!quiz.getNguoiDung().getMaNguoiDung().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this quiz");
        }

        quizzesRepository.delete(quiz);
    }
    public QuizWithQuestionsDTO getQuizById(UUID id) {
        BAI_KIEM_TRA quiz = quizzesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        QuizWithQuestionsDTO dto = new QuizWithQuestionsDTO();
        dto.setQuizzesID(quiz.getMaBaiKiemTra());
        dto.setTitle(quiz.getTieuDe());
        dto.setDescription(quiz.getMoTa());



//        List<QuestionDTO> questionDTOs = quiz.getQuestions().stream().map(q -> {
//            QuestionDTO qDto = new QuestionDTO();
//            qDto.setQuestionID(q.getQuestionID());
//            qDto.setQuestionName(q.getQuestionName());
//            qDto.setCorrectAnswer(q.getCorrectAnswer());
//
//
//
//            // Làm sạch dữ liệu options
//            String rawOptions = q.getOptions();
//            List<String> cleanedOptions = new ArrayList<>();
//            if (rawOptions != null && !rawOptions.isBlank()) {
//                cleanedOptions = Arrays.stream(rawOptions
//                                .replace("[", "")   // loại dấu [
//                                .replace("]", "")   // loại dấu ]
//                                .replace("\"", "")  // loại dấu "
//                                .split(","))
//                        .map(String::trim)         // loại bỏ khoảng trắng đầu/cuối
//                        .filter(s -> !s.isEmpty()) // loại bỏ chuỗi rỗng
//                        .toList();
//            }
//            qDto.setOptions(cleanedOptions);
//            qDto.setQuizId(q.getQuiz().getMaBaiKiemTra());
//            return qDto;
//        }).toList();
//
//        dto.setQuestions(questionDTOs);
        return dto;
    }


    // Convert Entity -> DTO
    private QuizDTO toDTO(BAI_KIEM_TRA entity) {
        QuizDTO dto = new QuizDTO();
        dto.setQuizzesID(entity.getMaBaiKiemTra());
        dto.setTitle(entity.getTieuDe());
        dto.setDescription(entity.getMoTa());
        //  Lấy UUID từ User entity
        if (entity.getNguoiDung() != null) {
            dto.setUserID(entity.getNguoiDung().getMaNguoiDung());
        }
        return dto;
    }



}
