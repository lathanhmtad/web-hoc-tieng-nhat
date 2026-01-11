package com.example.Oboe.Service;

import com.example.Oboe.DTOs.GrammarDTO;
import com.example.Oboe.DTOs.ReadingDTO;
import com.example.Oboe.Entity.NGU_PHAP;
import com.example.Oboe.Entity.CACH_DOC;
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
    public Map<String, Object> getAllGrammar(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NGU_PHAP> grammarPage = grammarRepository.getALl(search, pageable);

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

        NGU_PHAP nguPhap = new NGU_PHAP();
        nguPhap.setCauTruc(dto.getStructure());
        nguPhap.setGiaiThich(dto.getExplanation());
        nguPhap.setViDu(dto.getExample());
        nguPhap.setLoaiNguPhap(dto.getGrammarType());
        nguPhap.setTrinhDoJlpt(dto.getJlptLevel());

        NGU_PHAP saved = grammarRepository.save(nguPhap);

        return grammarToDTO(saved);
    }

    //  Get grammar by ID
    public GrammarDTO getGrammarById(UUID id) {
        NGU_PHAP nguPhap = grammarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Grammar với ID: " + id));
        return grammarToDTO(nguPhap);
    }

    //  Update grammar (ROLE_ADMIN)
    public GrammarDTO updateGrammar(UUID id, GrammarDTO dto) {
        checkAdminAccess();

        NGU_PHAP nguPhap = grammarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grammar không tồn tại"));

        if (dto.getStructure() != null) nguPhap.setCauTruc(dto.getStructure());
        if (dto.getExplanation() != null) nguPhap.setGiaiThich(dto.getExplanation());
        if (dto.getExample() != null) nguPhap.setViDu(dto.getExample());
        if (dto.getGrammarType() != null) nguPhap.setLoaiNguPhap(dto.getGrammarType());
        if(dto.getJlptLevel() != null) nguPhap.setTrinhDoJlpt(dto.getJlptLevel());

        NGU_PHAP updated = grammarRepository.save(nguPhap);
        return grammarToDTO(updated);
    }

    //  Delete grammar (ROLE_ADMIN)
    public void deleteGrammar(UUID id) {
        checkAdminAccess();

        NGU_PHAP nguPhap = grammarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grammar không tồn tại"));

        grammarRepository.delete(nguPhap);
    }

    //  Search grammar
    public List<GrammarDTO> searchGrammar(String keyword) {
        List<NGU_PHAP> nguPhaps = grammarRepository.searchGrammar(keyword); // cần @Query bên repo
        return nguPhaps.stream()
                .map(this::grammarToDTO)
                .collect(Collectors.toList());
    }

    //  Convert Grammar → DTO
    private GrammarDTO grammarToDTO(NGU_PHAP nguPhap) {
        GrammarDTO dto = new GrammarDTO();
        dto.setGrammarId(nguPhap.getMaNguPhap().toString());
        dto.setStructure(nguPhap.getCauTruc());
        dto.setExplanation(nguPhap.getGiaiThich());
        dto.setExample(nguPhap.getViDu());
        dto.setGrammarType(nguPhap.getLoaiNguPhap());
        dto.setJlptLevel(nguPhap.getTrinhDoJlpt());
        dto.setSentencesLinks(
                nguPhap.getMauCaus().stream()
                        .map(item -> GrammarDTO.SentencesLinks.builder()
                                .japaneseText(item.getCauTiengNhat())
                                .meaning(item.getNghiaTiengViet())
                                .build())
                        .toList()
        );

        return dto;
    }

    private ReadingDTO readingToDTO(CACH_DOC r) {
        return new ReadingDTO(
                r.getMaCachDoc(),
                r.getCachDocThucTe(),
                r.getLoaiDoc(),
                r.getLoaiDoiTuong(),
                r.getMaDoiTuong()
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
