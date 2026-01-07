package com.example.Oboe.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CommentDTOs {

    private UUID commentId;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private UUID userId;

    private String userName;

    private String fullName;

    private UUID commentIdParent;

    private UUID ReferenceId; // đại diện cho blog, kanji, hoặc loại nội dung khác

    private List<CommentDTOs> replies = new ArrayList<>();

    private String avatarUrl;

}
