package com.example.Oboe.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_comment_reply")
public class AICommentReply {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", referencedColumnName = "comment_id")
    private BinhLuan binhLuan;

    @Lob
    private String content;

    @Column(name = "created_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime createdAt;

    public AICommentReply() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getters & Setters =====

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BinhLuan getComment() {
        return binhLuan;
    }

    public void setComment(BinhLuan binhLuan) {
        this.binhLuan = binhLuan;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
