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


    public Map<String, Object> getAllKanji(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HanTu> kanjiPage = kanjiRepository.getAll(keyword, pageable);

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
        hanTu.setSoNet(dto.getStrokes());
        hanTu.setNghia(dto.getMeaning());
        hanTu.setKyTu(dto.getCharacterName());
        hanTu.setPhatAmTiengViet(dto.getVietnamesePronunciation());
        hanTu.setTrinhDoJLPT(dto.getJlptLevel());
        hanTu.setOnYomi(dto.getOnYomi());
        hanTu.setKunYomi(dto.getKunYomi());

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
            hanTu.setKyTu(dto.getCharacterName());
        }
        if (dto.getMeaning() != null) {
            hanTu.setNghia(dto.getMeaning());
        }
        if (dto.getStrokes() != null) {
            hanTu.setSoNet(dto.getStrokes());
        }
        if (dto.getVietnamesePronunciation() != null) {
            hanTu.setPhatAmTiengViet(dto.getVietnamesePronunciation());
        }
        if(dto.getJlptLevel() != null) {
            hanTu.setTrinhDoJLPT(dto.getJlptLevel());
        }
        if(dto.getOnYomi() != null) {
            hanTu.setOnYomi(dto.getOnYomi());
        }
        if(dto.getKunYomi() != null) {
            hanTu.setKunYomi(dto.getKunYomi());
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

        String meaning = hanTu.getNghia();
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
        dto.setKanjiId(hanTu.getMaHanTu());
        dto.setCharacterName(hanTu.getKyTu());
        dto.setMeaning(hanTu.getNghia());
        dto.setStrokes(hanTu.getSoNet());
        dto.setOnYomi(hanTu.getOnYomi());
        dto.setKunYomi(hanTu.getKunYomi());
        dto.setVietnamesePronunciation(hanTu.getPhatAmTiengViet());
        dto.setJlptLevel(hanTu.getTrinhDoJLPT());
        return dto;
    }
}
    