package com.example.Oboe.Service;

import com.example.Oboe.DTOs.CardItemDto;
import com.example.Oboe.DTOs.FlashCardDto;
import com.example.Oboe.Entity.ChiTietThe;
import com.example.Oboe.Entity.BoTheGhiNho;
import com.example.Oboe.Entity.NguoiDung;
import com.example.Oboe.Repository.FlashCardRepository;
import com.example.Oboe.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FlashCardService {

    @Autowired
    private FlashCardRepository flashCardRepository;
    private UserRepository userRepository;
    private UserService userService ;

    @Autowired
    public FlashCardService(FlashCardRepository flashCardRepository,
                            UserRepository userRepository,
                            UserService userService) {
        this.flashCardRepository = flashCardRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }


    public BoTheGhiNho createFlashCard(FlashCardDto dto, UUID userId) {
        NguoiDung nguoiDung = userRepository.findById(userId).orElse(null);
        if (nguoiDung == null) return null;

        BoTheGhiNho boTheGhiNho = new BoTheGhiNho();
        boTheGhiNho.setTenBoThe(dto.getTerm());
        boTheGhiNho.setMoTa(dto.getDescription());
        boTheGhiNho.setNguoiDung(nguoiDung);

        if (dto.getCardItems() != null) {
            for (CardItemDto itemDto : dto.getCardItems()) {
                ChiTietThe chiTietThe = new ChiTietThe();
                chiTietThe.setTuVung(itemDto.getWord());
                chiTietThe.setNghia(itemDto.getMeaning());
                chiTietThe.setBoTheGhiNho(boTheGhiNho);
                boTheGhiNho.getChiTietThes().add(chiTietThe);
            }
        }

        return flashCardRepository.save(boTheGhiNho);
    }

    public Page<BoTheGhiNho> getFlashCardsByUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao"));
        return flashCardRepository.findByUser(userId, pageable);
    }
    public List<FlashCardDto> getFlashcardById(UUID flashcardId) {
        return flashCardRepository.findById(flashcardId)
                .map(card -> List.of(convertToDto(card)))
                .orElse(List.of());
    }


    public Page<BoTheGhiNho> searchFlashCardsByTerm(UUID userId, String term, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));
        return flashCardRepository.searchByUserIdAndTerm(userId, term, pageable);
    }

     public List<BoTheGhiNho> getTop5LatestFlashCards(UUID userId) {
         return flashCardRepository.findTop5ByUserIdOrderByCreatedDesc(userId)
                 .stream()
                 .limit(5)
                 .toList();
     }

    public Optional<BoTheGhiNho> getFlashCardById(UUID cardId) {
        return flashCardRepository.findById(cardId);
    }

    @Transactional
    public boolean deleteFlashCard(UUID cardId, UUID userId) {
        Optional<BoTheGhiNho> optionalCard = flashCardRepository.findById(cardId);
        if (optionalCard.isEmpty()) return false;

        BoTheGhiNho card = optionalCard.get();
        if (!card.getNguoiDung().getMaNguoiDung().equals(userId)) return false;

        flashCardRepository.delete(card);
        return true;
    }

    public List<FlashCardDto> getAllflashByUserId(UUID userId) {
        Optional<NguoiDung> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return List.of();

        List<BoTheGhiNho> flashcards = flashCardRepository.findflashcardByUserId(userId);
        return flashcards.stream()
                .map(this::convertToDto)
                .toList();
    }


    public List<BoTheGhiNho> getAllFlashCards() {
        return flashCardRepository.findAll();
    }


    @Transactional
    public BoTheGhiNho updateFlashCard(UUID cardId, FlashCardDto dto, UUID userId) {
        Optional<BoTheGhiNho> optionalCard = flashCardRepository.findById(cardId);
        if (optionalCard.isEmpty()) return null;

        BoTheGhiNho boTheGhiNho = optionalCard.get();
        if (!boTheGhiNho.getNguoiDung().getMaNguoiDung().equals(userId)) return null;

        boTheGhiNho.setTenBoThe(dto.getTerm());
        boTheGhiNho.setMoTa(dto.getDescription());
        boTheGhiNho.getChiTietThes().clear();

        if (dto.getCardItems() != null) {
            for (CardItemDto itemDto : dto.getCardItems()) {
                ChiTietThe chiTietThe = new ChiTietThe();
                chiTietThe.setTuVung(itemDto.getWord());
                chiTietThe.setNghia(itemDto.getMeaning());
                chiTietThe.setBoTheGhiNho(boTheGhiNho);
                boTheGhiNho.getChiTietThes().add(chiTietThe);
            }
        }

        return flashCardRepository.save(boTheGhiNho);
    }

    private FlashCardDto convertToDto(BoTheGhiNho boTheGhiNho) {
        FlashCardDto dto = new FlashCardDto();
        dto.setTerm(boTheGhiNho.getTenBoThe());
        dto.setDescription(boTheGhiNho.getMoTa());
        dto.setFlashcardID(boTheGhiNho.getMaBoThe());
        List<CardItemDto> itemDtos = boTheGhiNho.getChiTietThes().stream()
                .map(item -> {
                    CardItemDto itemDto = new CardItemDto();
                    itemDto.setWord(item.getTuVung());
                    itemDto.setMeaning(item.getNghia());
                    return itemDto;
                })
                .toList();
        dto.setCardItems(itemDtos);
        return dto;
    }




}
