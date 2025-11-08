package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.CardItemDto;
import com.example.Oboe.Entity.MucThe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardItemRepository extends JpaRepository<MucThe, UUID> {
    @Query("SELECT new com.example.Oboe.DTOs.CardItemDto(c.word, c.meaning) FROM MucThe c WHERE c.boThe.set_id = :setId")
    List<CardItemDto> findAllByFlashCardId(@Param("setId") UUID setId);
} 