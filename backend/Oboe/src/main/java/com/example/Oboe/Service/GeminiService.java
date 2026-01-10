package com.example.Oboe.Service;

import com.example.Oboe.DTOs.QuestionDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    // Hàm cũ: generate list câu hỏi dạng DTO
    public List<QuestionDTO> generateQuestion(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> textMap = new HashMap<>();
        textMap.put("text", prompt);

        Map<String, Object> partMap = new HashMap<>();
        partMap.put("parts", List.of(textMap));
        partMap.put("role", "user");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(partMap));

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-goog-api-key", geminiApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    geminiApiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                if (body.containsKey("candidates")) {
                    List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
                    if (!candidates.isEmpty()) {
                        Map<String, Object> firstCandidate = candidates.get(0);
                        if (firstCandidate.containsKey("content")) {
                            Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
                            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

                            if (!parts.isEmpty() && parts.get(0).containsKey("text")) {
                                String rawResponse = parts.get(0).get("text").toString().trim();

                                // Cắt bỏ phần ngoài nếu cần
                                int jsonStart = rawResponse.indexOf("[");
                                int jsonEnd = rawResponse.lastIndexOf("]");
                                if (jsonStart >= 0 && jsonEnd > jsonStart) {
                                    rawResponse = rawResponse.substring(jsonStart, jsonEnd + 1);
                                }

                                ObjectMapper mapper = new ObjectMapper();

                                List<Map<String, Object>> rawList = mapper.readValue(rawResponse,
                                        new TypeReference<List<Map<String, Object>>>() {});
                                List<QuestionDTO> dtoList = new ArrayList<>();

                                for (Map<String, Object> item : rawList) {
                                    QuestionDTO dto = new QuestionDTO();
                                    dto.setQuestionID(UUID.randomUUID());
                                    dto.setQuestionName((String) item.get("question"));
                                    dto.setCorrectAnswer((String) item.get("answer"));

                                    Object choicesObj = item.get("choices");
                                    if (choicesObj instanceof List) {
                                        dto.setOptions((List<String>) choicesObj);
                                    }

                                    dtoList.add(dto);
                                }

                                return dtoList;
                            }
                        }
                    }
                }
                throw new RuntimeException("Không có nội dung hợp lệ từ Gemini.");
            } else {
                throw new RuntimeException("Lỗi HTTP: " + response.getStatusCode());
            }

        } catch (Exception ex) {
            throw new RuntimeException("Lỗi gọi Gemini API: " + ex.getMessage());
        }
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Hàm mới: gửi prompt và trả về nguyên văn text (chấm điểm, nhận xét,...)
    public String generateTextFromPrompt(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> textMap = new HashMap<>();
        textMap.put("text", prompt);

        Map<String, Object> partMap = new HashMap<>();
        partMap.put("parts", List.of(textMap));
        partMap.put("role", "user");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(partMap));

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-goog-api-key", geminiApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    geminiApiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                if (body.containsKey("candidates")) {
                    List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
                    if (!candidates.isEmpty()) {
                        Map<String, Object> firstCandidate = candidates.get(0);
                        if (firstCandidate.containsKey("content")) {
                            Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
                            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

                            if (!parts.isEmpty() && parts.get(0).containsKey("text")) {
                                String rawText = parts.get(0).get("text").toString().trim();

                                // ======= BỎ CÁC KÝ HIỆU CODE BLOCK ` ```json ... ``` ` =======
                                if (rawText.startsWith("```json")) {
                                    rawText = rawText.substring(7).trim(); // bỏ "```json"
                                } else if (rawText.startsWith("```")) {
                                    rawText = rawText.substring(3).trim(); // bỏ "```"
                                }

                                if (rawText.endsWith("```")) {
                                    rawText = rawText.substring(0, rawText.length() - 3).trim(); // bỏ ``` ở cuối
                                }

                                return rawText;
                            }
                        }
                    }
                }

                throw new RuntimeException("Không có nội dung hợp lệ từ Gemini.");
            } else {
                throw new RuntimeException("Lỗi HTTP: " + response.getStatusCode());
            }
        } catch (Exception ex) {
            throw new RuntimeException("Lỗi gọi Gemini API: " + ex.getMessage());
        }
    }

    public JsonNode parseJsonResponse(String jsonResponse) {
        try {
            return objectMapper.readTree(jsonResponse);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi phân tích JSON từ Gemini: " + e.getMessage());
        }
    }

    public String generateFlashcardContent(String topic, String kanjiMode, int quantity) {
        // Xây dựng chỉ thị cho AI
        StringBuilder prompt = new StringBuilder();
        prompt.append("Hãy tạo danh sách từ vựng tiếng Nhật về chủ đề: '").append(topic).append("'. ");
        prompt.append("Số lượng: ").append(quantity).append(" từ. ");

        // Xử lý logic Kanji/Hiragana
        if ("NO_KANJI".equals(kanjiMode)) {
            prompt.append("Yêu cầu: Chỉ sử dụng Hiragana/Katakana, không dùng Kanji. ");
        } else if ("KANJI_ONLY".equals(kanjiMode)) {
            prompt.append("Yêu cầu: Tập trung vào từ có Kanji. ");
        } else if("BOTH".equals(kanjiMode)) {
            prompt.append("Yêu cầu: Bao gồm cả Kanji và Hiragana. ");
        }

        // Quan trọng: Ép định dạng JSON để Frontend đọc được
        prompt.append("Trả về kết quả CHỈ LÀ MỘT JSON ARRAY thuần túy (không có markdown ```json), ");
        prompt.append("với cấu trúc mỗi phần tử: ");
        prompt.append("{ \"term\": \"từ tiếng Nhật\", \"mean\": \"nghĩa tiếng Việt\", \"example\": \"ví dụ câu (nếu có)\" }.");

        // Gọi hàm getContent (giả sử bạn đã có hàm gọi API Gemini trong service này)
//        return getContent(prompt.toString());
        return null;
    }

    public String generateQuizFromVocabularyList(List<String> vocabList, int quantity, String kanjiMode, String level) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Tạo một bài trắc nghiệm (Quiz) gồm ").append(quantity).append(" câu hỏi tiếng Nhật. ");
        prompt.append("Nội dung CHỈ được lấy từ danh sách từ vựng sau: ").append(String.join(", ", vocabList)).append(". ");

        if ("NO_KANJI".equals(kanjiMode)) {
            prompt.append("Yêu cầu: Câu hỏi và đáp án dùng Hiragana/Katakana, không dùng Kanji. ");
        } else if ("KANJI_ONLY".equals(kanjiMode)) {
            prompt.append("Yêu cầu: Ưu tiên hỏi về cách đọc Kanji hoặc ý nghĩa Hán tự. ");
        }

        prompt.append("Trả về định dạng JSON Array chuẩn (không markdown) với cấu trúc: ");
        prompt.append("[{\"question\": \"...\", \"options\": [\"A\", \"B\", \"C\", \"D\"], \"correctAnswer\": \"đáp án đúng\", \"type\": \"VOCABULARY\"}]");

        return null;
//        return getContent(prompt.toString());
    }
}
