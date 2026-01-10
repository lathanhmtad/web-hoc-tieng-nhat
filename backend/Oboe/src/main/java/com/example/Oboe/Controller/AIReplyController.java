package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.AIBlogReplyDTO;
import com.example.Oboe.Entity.AI_PHAN_HOI_BAI_VIET;
//import com.example.Oboe.Entity.AICommentReply;
import com.example.Oboe.Entity.BAI_VIET;
import com.example.Oboe.Entity.BINH_LUAN;
import com.example.Oboe.Repository.AIBlogReplyRepository;
//import com.example.Oboe.Repository.AICommentReplyRepository;
import com.example.Oboe.Repository.BlogRepository;
import com.example.Oboe.Repository.CommentRepository;
import com.example.Oboe.Service.GeminiService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai-reply")
@AllArgsConstructor
public class AIReplyController {

    private GeminiService geminiService;

    private BlogRepository blogRepository;

    private CommentRepository commentRepository;

    private AIBlogReplyRepository aiBlogReplyRepository;

//    @Autowired
//    private AICommentReplyRepository aiCommentReplyRepository;

    @PostMapping("/blog/{blogId}")
    public ResponseEntity<?> getOrCreateAIBlogReply(@PathVariable UUID blogId) {
        BAI_VIET baiViet = blogRepository.findById(blogId).orElse(null);
        if (baiViet == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Blog not found with ID: " + blogId);
            return ResponseEntity.status(404).body(error);
        }

        // Kiểm tra đã có phản hồi AI chưa
        AI_PHAN_HOI_BAI_VIET existingReply = aiBlogReplyRepository.findByBaiViet_maBaiViet(blogId);
        AIBlogReplyDTO dto = new AIBlogReplyDTO();

        if (existingReply != null) {
            // Dùng lại phản hồi đã có
            dto.setId(existingReply.getMaPhanHoi());
            dto.setBlogId(blogId);
            dto.setContent(existingReply.getNoiDung());
            dto.setCreatedAt(existingReply.getNgayTao());

            return ResponseEntity.ok(dto);
        }

        // Nếu chưa có, gọi Gemini
        String prompt = "Bạn là một AI chuyên viết bình luận giá trị cho các bài blog liên quan đến tiếng Nhật. Hãy dựa vào nội dung bài viết bên dưới để đưa ra phản hồi phù hợp:\n\n" +
                "Tiêu đề: " + baiViet.getTieuDe() +
                "\nNội dung: " + baiViet.getNoiDung() +
                "\n\nNếu đây là bài viết nêu câu hỏi hoặc vấn đề, hãy trả lời rõ ràng, đúng trọng tâm, giúp người viết hiểu và giải quyết triệt để vấn đề. Nếu đây là bài chia sẻ kinh nghiệm, tâm sự hay bí quyết học tập (ví dụ như 'Tôi đã hoàn thành khóa học Kanji như thế nào...'), hãy phản hồi một cách thân thiện, đồng cảm và cổ vũ tích cực. Luôn dùng giọng điệu thân thiện, không dùng markdown, câu văn rõ ràng, ngắn gọn nhưng đầy đủ ý nghĩa, giúp người đọc cảm thấy được lắng nghe và trân trọng." +
                " và hãy viết chỉ tầm khoảng dưới 150 chữ thôi";

        String response = geminiService.generateTextFromPrompt(prompt);

        // Tạo phản hồi mới
        AI_PHAN_HOI_BAI_VIET newReply = new AI_PHAN_HOI_BAI_VIET();
        newReply.setMaPhanHoi(UUID.randomUUID());
        newReply.setBaiViet(baiViet);
        newReply.setNoiDung(response);
        newReply.setNgayTao(LocalDateTime.now());

        aiBlogReplyRepository.save(newReply);

        dto.setId(newReply.getMaPhanHoi());
        dto.setBlogId(blogId);
        dto.setContent(newReply.getNoiDung());
        dto.setCreatedAt(newReply.getNgayTao());

        return ResponseEntity.ok(dto);
    }


    @PostMapping("/comment/{commentId}")
    public ResponseEntity<?> replyToComment(@PathVariable UUID commentId) {
        BINH_LUAN binhLuan = commentRepository.findById(commentId).orElse(null);
        if (binhLuan == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Comment not found with ID: " + commentId);
            return ResponseEntity.status(404).body(error);
        }

        String prompt = "Bạn là một AI chuyên phản hồi các bình luận trên blog. Hãy phản hồi lại bình luận sau:\n" +
                binhLuan.getNoiDung() +
                "\n\nHãy trả lời lịch sự, thân thiện, phù hợp ngữ cảnh. Không dùng markdown.";

        String response = geminiService.generateTextFromPrompt(prompt);

//        AICommentReply reply = new AICommentReply();
//        reply.setId(UUID.randomUUID());
//        reply.setComment(binhLuan);
//        reply.setContent(response);
//        reply.setCreatedAt(LocalDateTime.now());
//
//        aiCommentReplyRepository.save(reply);

        // Trả JSON
        Map<String, Object> result = new HashMap<>();
        result.put("commentId", commentId);
        result.put("reply", response);
        //result.put("createdAt", reply.getCreatedAt());

        return ResponseEntity.ok(result);
    }
}
