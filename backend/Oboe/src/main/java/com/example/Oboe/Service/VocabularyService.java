package com.example.Oboe.Service;

import com.example.Oboe.DTOs.SampleSentenceDTO;
import com.example.Oboe.DTOs.VocabularyDTOs;
import com.example.Oboe.DTOs.ReadingDTO;
import com.example.Oboe.Entity.CHI_TIET_TU_VUNG;
import com.example.Oboe.Entity.MAU_CAU;
import com.example.Oboe.Entity.TU_VUNG;
import com.example.Oboe.Entity.CACH_DOC;
import com.example.Oboe.Repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class VocabularyService {

    private VocabularyRepository vocabularyRepository;
    private ReadingRepository readingRepository;
    private KanjiRepository kanjiRepository;
    private ChiTietTuVungRepository chiTietTuVungRepository;
    private SampleSentenceRepository sampleSentenceRepository;

    // Get all vocabularies with pagination
    public Map<String, Object> getAllVocabulary(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TU_VUNG> vocabPage = vocabularyRepository.findAll(pageable);

        List<VocabularyDTOs> vocabDTOs = vocabPage.getContent().stream()
                .map(this::vocabToDTO).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("vocabularies", vocabDTOs);
        response.put("currentPage", vocabPage.getNumber());
        response.put("pageSize", vocabPage.getSize());
        response.put("totalElements", vocabPage.getTotalElements());
        response.put("totalPages", vocabPage.getTotalPages());
        response.put("isLastPage", vocabPage.isLast());

        return response;
    }

    // Create new vocabulary
    public VocabularyDTOs createVocabulary(VocabularyDTOs dto) {
        checkAdminAccess();

        TU_VUNG vocab = new TU_VUNG();
        vocab.setNoiDungTu(dto.getWords());
        vocab.setNghia(dto.getMeaning());
        vocab.setLoaiTu(dto.getWordType());
        vocab.setKieuChu(dto.getScriptType());
        vocab.setPhatAmTiengViet(dto.getVietnamese_pronunciation());

        TU_VUNG saved = vocabularyRepository.save(vocab);
        if (dto.getKanjiLinks() != null && !dto.getKanjiLinks().isEmpty()) {
            AtomicInteger orderCounter = new AtomicInteger(1);
            List<CHI_TIET_TU_VUNG> chiTietTuVung = dto.getKanjiLinks().stream().map(kanjiLink -> {
                CHI_TIET_TU_VUNG cttv = new CHI_TIET_TU_VUNG();
                cttv.setTuVung(saved);
                cttv.setHanTu(kanjiRepository.findById(kanjiLink.getValue())
                        .orElseThrow(() -> new RuntimeException("Kanji không tồn tại với ID: " + kanjiLink.getValue())));
                cttv.setThuTu(orderCounter.getAndIncrement());
                return cttv;
            }).toList();
            saved.setChiTietTuVungs(chiTietTuVung);
            chiTietTuVungRepository.saveAll(chiTietTuVung);
        }

        return vocabToDTO(saved);
    }

    // Get by ID
    public VocabularyDTOs getVocabularyById(UUID id) {
        TU_VUNG vocab = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy từ vựng với ID: " + id));
        return vocabToDTO(vocab);
    }

    // Update
    public VocabularyDTOs updateVocabulary(UUID id, VocabularyDTOs dto) {
        checkAdminAccess();

        TU_VUNG vocab = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Từ vựng không tồn tại"));

        if (dto.getWords() != null) vocab.setNoiDungTu(dto.getWords());
        if (dto.getMeaning() != null) vocab.setNghia(dto.getMeaning());
        if (dto.getWordType() != null) vocab.setLoaiTu(dto.getWordType());
        if (dto.getScriptType() != null) vocab.setKieuChu(dto.getScriptType());
        if (dto.getVietnamese_pronunciation() != null) vocab.setPhatAmTiengViet(dto.getVietnamese_pronunciation());
        List<CHI_TIET_TU_VUNG> chiTietTuVungs = List.of();
        chiTietTuVungRepository.deleteAll(vocab.getChiTietTuVungs());
        if (!dto.getKanjiLinks().isEmpty()) {
            AtomicInteger orderCounter = new AtomicInteger(1);
            chiTietTuVungs = dto.getKanjiLinks().stream().map(
                    kanjiLink -> {
                        CHI_TIET_TU_VUNG cttv = new CHI_TIET_TU_VUNG();
                        cttv.setTuVung(vocab);
                        cttv.setHanTu(kanjiRepository.findById(kanjiLink.getValue())
                                .orElseThrow(() -> new RuntimeException("Kanji không tồn tại với ID: " + kanjiLink.getValue())));
                        cttv.setThuTu(orderCounter.getAndIncrement());
                        return cttv;
                    }
            ).toList();
        }
        TU_VUNG updated = vocabularyRepository.save(vocab);
        updated.setChiTietTuVungs(chiTietTuVungRepository.saveAll(chiTietTuVungs));

        return vocabToDTO(updated);
    }

    // Delete
    public void deleteVocabulary(UUID id) {
        checkAdminAccess();

        TU_VUNG vocab = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Từ vựng không tồn tại"));

        List<CACH_DOC> cachDocs = readingRepository.findByLoaiDoiTuongAndMaDoiTuong("vocabulary", id);
        readingRepository.deleteAll(cachDocs);

        List<CHI_TIET_TU_VUNG> chiTietTuVungs = vocab.getChiTietTuVungs();
        chiTietTuVungRepository.deleteAll(chiTietTuVungs);

        vocabularyRepository.delete(vocab);
    }

    // Search
    public List<VocabularyDTOs> searchVocabulary(String keyword) {
        List<TU_VUNG> results = vocabularyRepository.searchVocabulary(keyword); // @Query cần định nghĩa
        return results.stream().map(this::vocabToDTO).collect(Collectors.toList());
    }

    // 🔄 Entity → DTO
    private VocabularyDTOs vocabToDTO(TU_VUNG vocab) {
        VocabularyDTOs dto = new VocabularyDTOs();
        dto.setVocalbId(vocab.getMaTuVung());
        dto.setWords(vocab.getNoiDungTu());
        dto.setMeaning(vocab.getNghia());
        dto.setWordType(vocab.getLoaiTu());
        dto.setScriptType(vocab.getKieuChu());

        if (vocab.getChiTietTuVungs() != null && !vocab.getChiTietTuVungs().isEmpty()) {
            dto.setKanjiLinks(
                    vocab.getChiTietTuVungs()
                            .stream()
                            .sorted(Comparator.comparing(CHI_TIET_TU_VUNG::getThuTu))
                            .map(
                                    item -> VocabularyDTOs.KanjiLink.builder()
                                            .label(String.format("%s - %s", item.getHanTu().getKyTu(), item.getHanTu().getNghia()))
                                            .value(item.getHanTu().getMaHanTu())
                                            .kiTu(item.getHanTu().getKyTu())
                                            .build())
                            .collect(Collectors.toList()));
        }
        dto.setListSampleSentences(
                sampleSentenceRepository.searchByVietnameseMeaning(vocab.getNoiDungTu())
                        .stream().map(item ->
                                SampleSentenceDTO.builder()
                                        .japaneseText(item.getCauTiengNhat())
                                        .vietnameseMeaning(item.getNghiaTiengViet())
                                        .build()

                                )
                        .toList()
        );
        dto.setVietnamese_pronunciation(vocab.getPhatAmTiengViet());
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

    private CACH_DOC readingToEntity(ReadingDTO dto) {
        CACH_DOC r = new CACH_DOC();
        r.setMaCachDoc(dto.getReadingID());
        r.setCachDocThucTe(dto.getReadingText());
        r.setLoaiDoc(dto.getReadingType());
        r.setLoaiDoiTuong(dto.getOwnerType());
        r.setMaDoiTuong(dto.getOwnerId());
        return r;
    }

    private void checkAdminAccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
                !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new SecurityException("Bạn không có quyền thực hiện thao tác này.");
        }
    }
}
