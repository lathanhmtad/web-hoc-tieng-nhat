package com.example.Oboe.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AIFlashcardRequest {
    String topic;
    String kanjiMode;
    private int quantity;
}
