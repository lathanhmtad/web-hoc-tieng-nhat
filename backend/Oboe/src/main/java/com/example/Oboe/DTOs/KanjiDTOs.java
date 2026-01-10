package com.example.Oboe.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class KanjiDTOs {
    private UUID kanjiId;
    private String characterName;
    private String meaning;
    private Integer strokes;
    private String VietnamesePronunciation;
    private String onYomi;
    private String kunYomi;
    private String jlptLevel;

}
