package com.example.Oboe.Service;

import com.example.Oboe.Entity.PHAN_HOI;
import com.example.Oboe.Repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public PHAN_HOI createFeedback(PHAN_HOI phanHoi) {
        return feedbackRepository.save(phanHoi);
    }

    public List<PHAN_HOI> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }
}
