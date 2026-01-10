package com.example.Oboe.Controller;

import com.example.Oboe.Entity.PHAN_HOI;
import com.example.Oboe.Service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // Gửi phản hồi
    @PostMapping
    public ResponseEntity<PHAN_HOI> submitFeedback(@RequestBody PHAN_HOI phanHoi) {
        PHAN_HOI saved = feedbackService.createFeedback(phanHoi);
        return ResponseEntity.ok(saved);
    }

    // Lấy tất cả phản hồi (chỉ dùng cho admin)
    @GetMapping
    public ResponseEntity<List<PHAN_HOI>> getAllFeedbacks() {
        return ResponseEntity.ok(feedbackService.getAllFeedbacks());
    }
}
