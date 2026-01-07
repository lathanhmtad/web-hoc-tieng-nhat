package com.example.Oboe.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class ReadingDTO {
    private UUID readingID;
    private String readingText;
    private String readingType;
    private String ownerType;
    private UUID ownerId;
}
