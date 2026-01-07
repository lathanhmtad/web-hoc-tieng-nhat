package com.example.Oboe.DTOs;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SampleSentenceDTO {
    private UUID id;
    private String japaneseText;
    private String vietnameseMeaning;
    private String reading;
    private GrammarLink grammarLink;

    @Getter
    @Setter
    @Builder
    public static class GrammarLink {
        private String label;
        private UUID value;
    }
}
