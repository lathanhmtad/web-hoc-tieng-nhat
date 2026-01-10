package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.*;
import com.example.Oboe.Entity.CHI_TIET_THE;
import com.example.Oboe.Entity.BO_THE_GHI_NHO;
import com.example.Oboe.Repository.CardItemRepository;
import com.example.Oboe.Repository.FlashCardRepository;
import com.example.Oboe.Service.GeminiService;
import com.example.Oboe.annotation.PremiumOnly;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.example.Oboe.Config.CustomUserDetails;

import java.util.*;

@RestController

@RequestMapping("/api/ai")
@AllArgsConstructor
public class AIController {
    private CardItemRepository cardItemRepository;
    private GeminiService geminiService;
    private FlashCardRepository flashCardRepository;

    @PremiumOnly
    @PostMapping("/generate-flashcards")
    public ResponseEntity<?> generateFlashcards(@RequestBody AIFlashcardRequest request) {
        try {
            String jsonResponse = geminiService.generateFlashcardContent(
                    request.getTopic(),
                    request.getKanjiMode(),
                    request.getQuantity()
            );
            // Trả về string JSON để frontend tự parse
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi tạo flashcard: " + e.getMessage());
        }
    }

    @PremiumOnly
    @PostMapping("/generate-question")
    public List<QuestionDTO> generateQuestionsByUser(
            @RequestBody QuizFormRequest request) {

        List<BO_THE_GHI_NHO> boTheGhiNhoList = flashCardRepository.findAllById(request.getFlashcardIds());

        // Nếu có flashcard → xử lý theo logic cũ
        if (!boTheGhiNhoList.isEmpty()) {
            List<String> wordMeaningList = new ArrayList<>();
            for (BO_THE_GHI_NHO flashCard : boTheGhiNhoList) {
                for (CHI_TIET_THE chiTietThe : flashCard.getChiTietThes()) {
                    String word = chiTietThe.getTuVung();
                    String meaning = chiTietThe.getNghia();
                    if (word != null && meaning != null) {
                        wordMeaningList.add(word + " : " + meaning);
                    }
                }
            }

            if (wordMeaningList.isEmpty()) {
                throw new RuntimeException("Không có từ vựng nào trong flashcards của người dùng.");
            }

            // 3. Xử lý các tham số mặc định nếu null
            int quantity = request.getQuantity() > 0 ? request.getQuantity() : 5; // Mặc định 5 câu
            String level = request.getLevel() != null ? request.getLevel() : "N5"; // Mặc định N5
            String kanjiMode = request.getKanjiMode() != null ? request.getKanjiMode() : "MIXED"; // Mặc định NO_KANJI

            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("Bạn là một giáo viên tiếng Nhật. Hãy tạo danh sách câu hỏi trắc nghiệm dựa trên các từ vựng sau:\n");
            for (String entry : wordMeaningList) {
                promptBuilder.append("- ").append(entry).append("\n");
            }

            promptBuilder.append("\n--------------------------\n");
            promptBuilder.append("YÊU CẦU TẠO CÂU HỎI:\n");

            promptBuilder.append(String.format("1. Số lượng câu hỏi: %d câu.\n", quantity));

            promptBuilder.append(String.format("2. Trình độ (độ khó): Tương đương JLPT %s.\n", level));

            // Sử dụng kanjiMode để điều hướng cách hỏi
            promptBuilder.append("3. Chế độ câu hỏi (Kanji Mode): ");
            switch (kanjiMode.toUpperCase()) {
                case "NO_KANJI":
                    promptBuilder.append("TUYỆT ĐỐI KHÔNG DÙNG KANJI. Hãy chuyển tất cả các từ tiếng Nhật trong câu hỏi và đáp án sang dạng HIRAGANA hoặc KATAKANA.\n");
                    break;
                case "KANJI_ONLY":
                    promptBuilder.append("ƯU TIÊN DÙNG KANJI. Tất cả các từ vựng tiếng Nhật phải được viết bằng Kanji (nếu từ đó có chữ Hán).\n");
                    break;
                case "MIXED":
                default:
                    promptBuilder.append("KẾT HỢP (MIXED). Hãy trộn lẫn ngẫu nhiên: có câu dùng toàn Hiragana, có câu dùng Kanji, để kiểm tra đa dạng.\n");
            }

            // Định dạng Output JSON
            promptBuilder.append("""
                    4. Định dạng trả về:
                    - Chỉ trả về JSON array, không kèm markdown (```json).
                    - Cấu trúc:
                    [
                        {
                            "question": "Nội dung câu hỏi tiếng Nhật",
                            "choices": ["A", "B", "C", "D"],
                            "answer": "Đáp án đúng (phải giống hệt 1 trong 4 choices)"
                        }
                    ]
                    """);

            return geminiService.generateQuestion(promptBuilder.toString());
        } else {
            // Nếu không có flashcard → fallback random CardItem
            List<CHI_TIET_THE> allChiTietThes = cardItemRepository.findAll();
            if (allChiTietThes.isEmpty()) {
                throw new RuntimeException("Không tìm thấy CardItem nào trong cơ sở dữ liệu.");
            }

            Random random = new Random();
            CHI_TIET_THE randomChiTietThe = allChiTietThes.get(random.nextInt(allChiTietThes.size()));
            String prompt = buildPrompt(randomChiTietThe);
            return geminiService.generateQuestion(prompt);
        }
    }

    private String buildPrompt(CHI_TIET_THE chiTietThe) {
        return """
                Tạo ra đúng 10 câu hỏi trắc nghiệm tiếng Nhật dựa trên từ vựng sau:
                
                - Từ vựng: "%s"
                - Nghĩa tiếng Việt: "%s"
                
                Yêu cầu:
                1. Mỗi câu hỏi có 4 lựa chọn .
                2. Chỉ 1 đáp án đúng.
                3. Trả về định dạng JSON như sau:
                
                [
                    {
                        "question": "Câu hỏi",
                        "choices": [
                            "lựa chọn A",
                            "lựa chọn B",
                            "lựa chọn C",
                            "lựa chọn D"
                        ],
                        "answer": "Đáp án đúng"
                    }
                ]
                
                Không thêm giải thích hay văn bản nào ngoài JSON.
                """.formatted(chiTietThe.getTuVung(), chiTietThe.getNghia());
    }

    @PremiumOnly
    @PostMapping("/evaluate")
    public String evaluateAnswers(@RequestBody UserAnswerAIDTO request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("""
                Bạn là một giáo viên chấm bài thi tiếng Nhật.
                Hãy dựa vào từng câu hỏi, đáp án đúng, và câu trả lời của người dùng để:
                - Đưa ra điểm tổng kết theo thang điểm 100.
                - Phân tích rõ câu nào đúng, câu nào sai nếu câu nào sai hãy đưa ra lời giải thích giúp cải thiện.
                - Đưa ra nhận xét tổng thể cho toàn bài làm.
                
                Dưới đây là danh sách câu hỏi và câu trả lời:
                """);

        int index = 1;
        for (AnsweredQuestionDTO q : request.getAnswers()) {
            prompt.append(String.format("""
                    Câu %d:
                    - Câu hỏi: %s
                    - Phương án: %s
                    - Đáp án đúng: %s
                    - Câu trả lời của người dùng: %s
                    
                    """, index++, q.getQuestionName(), q.getOptions(), q.getCorrectAnswer(), q.getUserAnswer()));
        }

        prompt.append("""
                        Hãy trả kết quả theo định dạng:
                        {
                          "score": 85,
                          "results": [
                            {
                              "question": "Câu hỏi 1",
                              "correct": true,
                              "phanHoi": "Bạn trả lời đúng"
                            },
                            ...
                          ],
                          "binhLuan": "Hãy viết phần nhận xét tổng thể theo phong cách thân thiện, rõ ràng và mang tính hỗ trợ học tập. Nội dung cần có:
                          1. Đánh giá trình độ hiện tại (ví dụ: bạn đang ở khoảng N5 hoặc đầu N4).
                          2. Nhận xét các phần làm tốt (ví dụ: bạn làm tốt phần từ vựng chủ đề trường học).
                          3. Phân tích các lỗi sai chính, ví dụ: sai mẫu ngữ pháp như ～ている hoặc hiểu sai nghĩa của từ đồng âm.
                          4. Gợi ý cụ thể để cải thiện, ví dụ:
                             - 'Bạn nên ôn lại mẫu ngữ pháp N5 như ～ませんか, ～ましょう bằng cách làm bài tập trong sách Minna no Nihongo Bài 5 đến Bài 8.'
                             - 'Bạn có thể luyện nghe các đoạn hội thoại ngắn về chủ đề gia đình để cải thiện khả năng phản xạ.'
                          5. Động viên hoặc khen ngợi, ví dụ: 'Tiến bộ rất tốt! Chỉ cần luyện tập thêm một chút là bạn có thể đạt N4. Hãy tiếp tục cố gắng nhé!'"
                }
                Không thêm dấu ``` hoặc định dạng markdown nào. Chỉ JSON thuần.
                """);

        try {
            String response = geminiService.generateTextFromPrompt(prompt.toString());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Lỗi khi đánh giá câu trả lời\"}";
        }
    }

    @PremiumOnly
    @PostMapping("/translate")
    public Map<String, String> translateJapaneseToVietnamese(@RequestBody Map<String, String> request) {
        String input = request.get("text");
        String prompt = """
                Bạn là giáo viên dạy tiếng Nhật. Bạn hãy đưa ra câu trả lời một cách trôi chảy liền mạch. Vào đầu câu trả lời sẽ có chào bạn..
                Dịch câu tiếng Nhật sau sang tiếng Việt. Nếu là câu hỏi thì hãy dịch sao cho người học dễ hiểu.
                Nếu câu có cấu trúc ngữ pháp đặc biệt, hãy giải thích ngắn gọn ý nghĩa mẫu ngữ pháp đó. Giải thích đầy đủ về:
                - Nghĩa của từng từ trong câu
                - Cấu trúc ngữ pháp được sử dụng
                - Ngữ cảnh phù hợp để dùng câu này
                - Hoàn cảnh sử dụng, tình huống sử dụng, đưa ra ví dụ
                - Sắc thái, mức độ lịch sự (nếu có)
                - Những lưu ý khi sử dụng câu này trong giao tiếp hàng ngày.
                - Viết cho tôi dưới 400 ký tự và hãy trả lời luôn
                
                Câu: %s
                
                Hãy trả kết quả dạng văn bản thuần, không cần định dạng markdown, không thêm bất kỳ văn bản thừa nào.
                """.formatted(input);

        try {
            String response = geminiService.generateTextFromPrompt(prompt);

            // Trả về kết quả dưới dạng JSON với key "explanation"
            Map<String, String> result = new HashMap<>();
            result.put("explanation", response);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi dịch flashcard: " + e.getMessage());
            return error;
        }
    }

}
