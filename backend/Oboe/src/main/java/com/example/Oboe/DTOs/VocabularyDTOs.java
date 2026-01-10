package com.example.Oboe.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;

@Getter
@Setter
public class VocabularyDTOs {

    private UUID vocalbId;
    private String words;
    private String meanning;
    private String wordType; // noun, verb, adj,...
    private String scriptType; // hiragana, katakana, kanji
    private List<KanjiLink> kanjiLinks;
    private String vietnamese_pronunciation;
    private List<ReadingDTO> readings;
    private String readingRomaji;
    private List<SampleSentenceDTO> listSampleSentences;

    @Getter
    @Setter
    @Builder
    public static class KanjiLink {
        private String label;
        private UUID value;
        private String kiTu;
    }
}
