package com.example.Oboe.Service;

import com.example.Oboe.DTOs.CardItemDto;
import com.example.Oboe.DTOs.FlashCardDto;
import com.example.Oboe.Entity.MucThe;
import com.example.Oboe.Entity.BoThe;
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


    public BoThe createFlashCard(FlashCardDto dto, UUID userId) {
        NguoiDung nguoiDung = userRepository.findById(userId).orElse(null);
        if (nguoiDung == null) return null;

        BoThe boThe = new BoThe();
        boThe.setTerm(dto.getTerm());
        boThe.setDescription(dto.getDescription());
        boThe.setUser(nguoiDung);

        if (dto.getCardItems() != null) {
            for (CardItemDto itemDto : dto.getCardItems()) {
                MucThe mucThe = new MucThe();
                mucThe.setWord(itemDto.getWord());
                mucThe.setMeaning(itemDto.getMeaning());
                mucThe.setBoThe(boThe);
                boThe.getMucThes().add(mucThe);
            }
        }

        return flashCardRepository.save(boThe);
    }

    public Page<BoThe> getFlashCardsByUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));
        return flashCardRepository.findByUser(userId, pageable);
    }
    public List<FlashCardDto> getFlashcardById(UUID flashcardId) {
        return flashCardRepository.findById(flashcardId)
                .map(card -> List.of(convertToDto(card)))
                .orElse(List.of());
    }


    public Page<BoThe> searchFlashCardsByTerm(UUID userId, String term, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));
        return flashCardRepository.searchByUserIdAndTerm(userId, term, pageable);
    }

     public List<BoThe> getTop5LatestFlashCards(UUID userId) {
         return flashCardRepository.findTop5ByUserIdOrderByCreatedDesc(userId)
                 .stream()
                 .limit(5)
                 .toList();
     }

    public Optional<BoThe> getFlashCardById(UUID cardId) {
        return flashCardRepository.findById(cardId);
    }

    @Transactional
    public boolean deleteFlashCard(UUID cardId, UUID userId) {
        Optional<BoThe> optionalCard = flashCardRepository.findById(cardId);
        if (optionalCard.isEmpty()) return false;

        BoThe card = optionalCard.get();
        if (!card.getUser().getUser_id().equals(userId)) return false;

        flashCardRepository.delete(card);
        return true;
    }

    public List<FlashCardDto> getAllflashByUserId(UUID userId) {
        Optional<NguoiDung> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return List.of();

        List<BoThe> flashcards = flashCardRepository.findflashcardByUserId(userId);
        return flashcards.stream()
                .map(this::convertToDto)
                .toList();
    }


    public List<BoThe> getAllFlashCards() {
        return flashCardRepository.findAll();
    }


    @Transactional
    public BoThe updateFlashCard(UUID cardId, FlashCardDto dto, UUID userId) {
        Optional<BoThe> optionalCard = flashCardRepository.findById(cardId);
        if (optionalCard.isEmpty()) return null;

        BoThe boThe = optionalCard.get();
        if (!boThe.getUser().getUser_id().equals(userId)) return null;

        boThe.setTerm(dto.getTerm());
        boThe.setDescription(dto.getDescription());
        boThe.getMucThes().clear();

        if (dto.getCardItems() != null) {
            for (CardItemDto itemDto : dto.getCardItems()) {
                MucThe mucThe = new MucThe();
                mucThe.setWord(itemDto.getWord());
                mucThe.setMeaning(itemDto.getMeaning());
                mucThe.setBoThe(boThe);
                boThe.getMucThes().add(mucThe);
            }
        }

        return flashCardRepository.save(boThe);
    }

    private FlashCardDto convertToDto(BoThe boThe) {
        FlashCardDto dto = new FlashCardDto();
        dto.setTerm(boThe.getTerm());
        dto.setDescription(boThe.getDescription());
        dto.setFlashcardID(boThe.getSet_id());
        List<CardItemDto> itemDtos = boThe.getMucThes().stream()
                .map(item -> {
                    CardItemDto itemDto = new CardItemDto();
                    itemDto.setWord(item.getWord());
                    itemDto.setMeaning(item.getMeaning());
                    return itemDto;
                })
                .toList();
        dto.setCardItems(itemDtos);
        return dto;
    }




}
