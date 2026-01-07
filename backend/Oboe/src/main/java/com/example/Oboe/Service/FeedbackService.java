package com.example.Oboe.Service;

import com.example.Oboe.Entity.PhanHoi;
import com.example.Oboe.Repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public PhanHoi createFeedback(PhanHoi phanHoi) {
        return feedbackRepository.save(phanHoi);
    }

    public List<PhanHoi> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }
}
