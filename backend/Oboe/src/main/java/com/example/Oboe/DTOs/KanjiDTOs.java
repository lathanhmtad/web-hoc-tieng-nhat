package com.example.Oboe.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
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
    private List<VocabLink> vocabLinks;

    @Getter
    @Setter
    @Builder
    public static class VocabLink {
        private String word;
        private String meaning;
    }
}
