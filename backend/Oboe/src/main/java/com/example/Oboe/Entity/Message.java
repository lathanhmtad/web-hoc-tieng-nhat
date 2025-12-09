package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "messageid", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID messageID;


    private String sent_message;

    private LocalDateTime sent_at = LocalDateTime.now();

    // Message relationships
    @ManyToOne
    @JoinColumn(name = "senderid")
    @JsonBackReference("user-sent-messages")
    private NguoiDung sender;

    @ManyToOne
    @JoinColumn(name = "receiverid")
    @JsonBackReference("user-received-messages")
    private NguoiDung receiver;

    @PreUpdate
    public void preUpdate() {
        this.sent_at = LocalDateTime.now();
    }

    // Getters & Setters

    public UUID getMessageID() {
        return messageID;
    }

    public void setMessageID(UUID messageID) {
        this.messageID = messageID;
    }

    public String getSent_message() {
        return sent_message;
    }

    public void setSent_message(String sent_message) {
        this.sent_message = sent_message;
    }

    public LocalDateTime getSent_at() {
        return sent_at;
    }

    public void setSent_at(LocalDateTime sent_at) {
        this.sent_at = sent_at;
    }

    public NguoiDung getSender() {
        return sender;
    }

    public void setSender(NguoiDung sender) {
        this.sender = sender;
    }

    public NguoiDung getReceiver() {
        return receiver;
    }

    public void setReceiver(NguoiDung receiver) {
        this.receiver = receiver;
    }

    public UUID getSenderId() {
        return sender != null ? sender.getMaNguoiDung() : null;
    }

    public UUID getReceiverId() {
        return receiver != null ? receiver.getMaNguoiDung() : null;
    }
}
