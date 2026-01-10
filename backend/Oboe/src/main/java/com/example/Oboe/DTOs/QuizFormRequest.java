package com.example.Oboe.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class QuizFormRequest {
    private List<UUID> flashcardIds;
    private int quantity;
    private String kanjiMode;
    private String level;
}
