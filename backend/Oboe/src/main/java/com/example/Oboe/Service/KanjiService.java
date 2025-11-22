package com.example.Oboe.Service;

import com.example.Oboe.DTOs.KanjiDTOs;
import com.example.Oboe.Entity.HanTu;
import com.example.Oboe.Repository.KanjiRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class KanjiService {

    private final KanjiRepository kanjiRepository;

    public KanjiService(KanjiRepository kanjiRepository) {
        this.kanjiRepository = kanjiRepository;
    }


    public Map<String, Object> getAllKanji(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HanTu> kanjiPage = kanjiRepository.findAll(pageable);

        List<KanjiDTOs> kanjiDTOs = kanjiPage.getContent()
                .stream()
                .map(this::kanjiToDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("kanjis", kanjiDTOs);
        response.put("currentPage", kanjiPage.getNumber());
        response.put("pageSize", kanjiPage.getSize());
        response.put("totalElements", kanjiPage.getTotalElements());
        response.put("totalPages", kanjiPage.getTotalPages());
        response.put("isLastPage", kanjiPage.isLast());

        return response;
    }


    public KanjiDTOs createKanji(KanjiDTOs dto){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new SecurityException("Bạn không có quyền tạo Kanji.");
        }

        HanTu hanTu = new HanTu();
        hanTu.setStrokes(dto.getStrokes());
        hanTu.setMeaning(dto.getMeaning());
        hanTu.setCharacter_name(dto.getCharacterName());
        hanTu.setVietnamesePronunciation(dto.getVietnamesePronunciation());

        HanTu saved = kanjiRepository.save(hanTu);
        return kanjiToDTO(saved);
    }


    public KanjiDTOs getKanjiByKanjiId(UUID kanjiId) {
        HanTu hanTu = kanjiRepository.findById(kanjiId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Kanji với ID: " + kanjiId));
        return kanjiToDTO(hanTu);
    }


    public KanjiDTOs updateKanji(KanjiDTOs dto, UUID kanjiId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
                !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new SecurityException("Bạn không có quyền cập nhật Kanji.");
        }

        HanTu hanTu = getKanjiEntityById(kanjiId);
        if (hanTu == null) return null;

        if (dto.getCharacterName() != null) {
            hanTu.setCharacter_name(dto.getCharacterName());
        }
        if (dto.getMeaning() != null) {
            hanTu.setMeaning(dto.getMeaning());
        }
        if (dto.getStrokes() != null) {
            hanTu.setStrokes(dto.getStrokes());
        }
        if (dto.getVietnamesePronunciation() != null) {
            hanTu.setVietnamesePronunciation(dto.getVietnamesePronunciation());
        }

        HanTu updated = kanjiRepository.save(hanTu);
        return kanjiToDTO(updated);
    }


    public void deleteKanji(UUID kanjiId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
                !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new SecurityException("Bạn không có quyền xóa Kanji.");
        }

        HanTu hanTu = getKanjiEntityById(kanjiId);
        if (hanTu == null) throw new RuntimeException("Không tìm thấy Kanji với ID: " + kanjiId);

        kanjiRepository.delete(hanTu);
    }


    public List<KanjiDTOs> searchKanji(String keyword) {
        List<HanTu> hanTus = kanjiRepository.searchKanji(keyword);
        return hanTus.stream()
                .map(this::kanjiToDTO)
                .collect(Collectors.toList());
    }


    public List<KanjiDTOs> getRelatedKanji(UUID kanjiId) {
        HanTu hanTu = kanjiRepository.findById(kanjiId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Kanji"));

        String meaning = hanTu.getMeaning();
        List<HanTu> relatedHanTus = kanjiRepository.findRelatedByMeaning(meaning, kanjiId);

        return relatedHanTus.stream()
                .map(this::kanjiToDTO)
                .collect(Collectors.toList());
    }


    public HanTu getKanjiEntityById(UUID kanjiId) {
        return kanjiRepository.findById(kanjiId).orElse(null);
    }


    public KanjiDTOs kanjiToDTO(HanTu hanTu) {
        KanjiDTOs dto = new KanjiDTOs();
        dto.setKanjiId(hanTu.getKanjiId());
        dto.setCharacterName(hanTu.getCharacter_name());
        dto.setMeaning(hanTu.getMeaning());
        dto.setStrokes(hanTu.getStrokes());
        dto.setVietnamesePronunciation(hanTu.getVietnamesePronunciation());
        return dto;
    }
}
    