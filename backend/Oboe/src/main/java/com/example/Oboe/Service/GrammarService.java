package com.example.Oboe.Service;

import com.example.Oboe.DTOs.GrammarDTO;
import com.example.Oboe.DTOs.ReadingDTO;
import com.example.Oboe.Entity.NguPhap;
import com.example.Oboe.Entity.Reading;
import com.example.Oboe.Repository.GrammarRepository;
import com.example.Oboe.Repository.ReadingRepository;
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
public class GrammarService {

    private final GrammarRepository grammarRepository;
    private final ReadingRepository readingRepository;

    public GrammarService(GrammarRepository grammarRepository,ReadingRepository readingRepository) {
        this.grammarRepository = grammarRepository;
        this.readingRepository = readingRepository;
    }

    // Get all grammar with pagination
    public Map<String, Object> getAllGrammar(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NguPhap> grammarPage = grammarRepository.findAll(pageable);

        List<GrammarDTO> grammarDTOs = grammarPage.getContent()
                .stream()
                .map(this::grammarToDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("grammars", grammarDTOs);
        response.put("currentPage", grammarPage.getNumber());
        response.put("pageSize", grammarPage.getSize());
        response.put("totalElements", grammarPage.getTotalElements());
        response.put("totalPages", grammarPage.getTotalPages());
        response.put("isLastPage", grammarPage.isLast());

        return response;
    }

    //  Create new grammar (ROLE_ADMIN)
    public GrammarDTO createGrammar(GrammarDTO dto) {
        checkAdminAccess();

        NguPhap nguPhap = new NguPhap();
        nguPhap.setStructure(dto.getStructure());
        nguPhap.setExplanation(dto.getExplanation());
        nguPhap.setExample(dto.getExample());
        nguPhap.setGrammarType(dto.getGrammarType());
        nguPhap.setVietnamesePronunciation(dto.getVietnamesePronunciation());

        NguPhap saved = grammarRepository.save(nguPhap);

        return grammarToDTO(saved);
    }

    //  Get grammar by ID
    public GrammarDTO getGrammarById(UUID id) {
        NguPhap nguPhap = grammarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Grammar với ID: " + id));
        return grammarToDTO(nguPhap);
    }

    //  Update grammar (ROLE_ADMIN)
    public GrammarDTO updateGrammar(UUID id, GrammarDTO dto) {
        checkAdminAccess();

        NguPhap nguPhap = grammarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grammar không tồn tại"));

        if (dto.getStructure() != null) nguPhap.setStructure(dto.getStructure());
        if (dto.getExplanation() != null) nguPhap.setExplanation(dto.getExplanation());
        if (dto.getExample() != null) nguPhap.setExample(dto.getExample());
        if (dto.getGrammarType() != null) nguPhap.setGrammarType(dto.getGrammarType());
        if (dto.getVietnamesePronunciation() != null) nguPhap.setVietnamesePronunciation(dto.getVietnamesePronunciation());

        NguPhap updated = grammarRepository.save(nguPhap);
        return grammarToDTO(updated);
    }

    //  Delete grammar (ROLE_ADMIN)
    public void deleteGrammar(UUID id) {
        checkAdminAccess();

        NguPhap nguPhap = grammarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grammar không tồn tại"));

        grammarRepository.delete(nguPhap);
    }

    //  Search grammar
    public List<GrammarDTO> searchGrammar(String keyword) {
        List<NguPhap> nguPhaps = grammarRepository.searchGrammar(keyword); // cần @Query bên repo
        return nguPhaps.stream()
                .map(this::grammarToDTO)
                .collect(Collectors.toList());
    }

    //  Convert Grammar → DTO
    private GrammarDTO grammarToDTO(NguPhap nguPhap) {
        GrammarDTO dto = new GrammarDTO();
        dto.setGrammarId(nguPhap.getGrammaID().toString());
        dto.setStructure(nguPhap.getStructure());
        dto.setExplanation(nguPhap.getExplanation());
        dto.setExample(nguPhap.getExample());
        dto.setGrammarType(nguPhap.getGrammarType());
        dto.setVietnamesePronunciation(nguPhap.getVietnamesePronunciation());
        List<ReadingDTO> readingDTOs = readingRepository.findByOwnerTypeAndOwnerId("grammar", nguPhap.getGrammaID())
                .stream().map(this::readingToDTO).collect(Collectors.toList());
        dto.setReadings(readingDTOs);
        return dto;
    }

    private ReadingDTO readingToDTO(Reading r) {
        return new ReadingDTO(
                r.getReadingID(),
                r.getReadingText(),
                r.getReadingType(),
                r.getOwnerType(),
                r.getOwnerId()
        );
    }

    //  Check role
    private void checkAdminAccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
                !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new SecurityException("Bạn không có quyền thực hiện thao tác này.");
        }
    }
}
