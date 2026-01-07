package com.example.Oboe.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GrammarDTO {
    private String grammarId;
    private String example;
    private String explanation;
    private String structure;
    private String grammarType;
    private String jlptLevel;
    private List<ReadingDTO> readings;

}
