package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.CardItemDto;
import com.example.Oboe.Entity.ChiTietThe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardItemRepository extends JpaRepository<ChiTietThe, UUID> {
    @Query("SELECT new com.example.Oboe.DTOs.CardItemDto(c.tuVung, c.nghia) FROM ChiTietThe c WHERE c.boTheGhiNho.maBoThe = :setId")
    List<CardItemDto> findAllByFlashCardId(@Param("setId") UUID setId);
} 