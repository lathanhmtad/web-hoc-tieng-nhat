package com.example.Oboe.Service;

import com.example.Oboe.DTOs.VocabularyDTOs;
import com.example.Oboe.DTOs.ReadingDTO;
import com.example.Oboe.Entity.ChiTietTuVung;
import com.example.Oboe.Entity.TuVung;
import com.example.Oboe.Entity.CachDoc;
import com.example.Oboe.Repository.ChiTietTuVungRepository;
import com.example.Oboe.Repository.KanjiRepository;
import com.example.Oboe.Repository.VocabularyRepository;
import com.example.Oboe.Repository.ReadingRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final ReadingRepository readingRepository;
    private final KanjiRepository kanjiRepository;
    private final ChiTietTuVungRepository chiTietTuVungRepository;

    public VocabularyService(VocabularyRepository vocabularyRepository, ReadingRepository readingRepository, KanjiRepository kanjiRepository, ChiTietTuVungRepository chiTietTuVungRepository) {
        this.vocabularyRepository = vocabularyRepository;
        this.readingRepository = readingRepository;
        this.kanjiRepository = kanjiRepository;
        this.chiTietTuVungRepository = chiTietTuVungRepository;
    }

    // Get all vocabularies with pagination
    public Map<String, Object> getAllVocabulary(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TuVung> vocabPage = vocabularyRepository.findAll(pageable);

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

        TuVung vocab = new TuVung();
        vocab.setNoiDungTu(dto.getWords());
        vocab.setNghia(dto.getMeanning());
        vocab.setLoaiTu(dto.getWordType());
        vocab.setKieuChu(dto.getScriptType());
        vocab.setPhatAmTiengViet(dto.getVietnamese_pronunciation());

        TuVung saved = vocabularyRepository.save(vocab);
        if (dto.getKanjiLinks() != null && !dto.getKanjiLinks().isEmpty()) {
            AtomicInteger orderCounter = new AtomicInteger(1);
            List<ChiTietTuVung> chiTietTuVung = dto.getKanjiLinks().stream().map(kanjiLink -> {
                ChiTietTuVung cttv = new ChiTietTuVung();
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
        TuVung vocab = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy từ vựng với ID: " + id));
        return vocabToDTO(vocab);
    }

    // Update
    public VocabularyDTOs updateVocabulary(UUID id, VocabularyDTOs dto) {
        checkAdminAccess();

        TuVung vocab = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Từ vựng không tồn tại"));

        if (dto.getWords() != null) vocab.setNoiDungTu(dto.getWords());
        if (dto.getMeanning() != null) vocab.setNghia(dto.getMeanning());
        if (dto.getWordType() != null) vocab.setLoaiTu(dto.getWordType());
        if (dto.getScriptType() != null) vocab.setKieuChu(dto.getScriptType());
        if (dto.getVietnamese_pronunciation() != null) vocab.setPhatAmTiengViet(dto.getVietnamese_pronunciation());
        List<ChiTietTuVung> chiTietTuVungs = List.of();
        chiTietTuVungRepository.deleteAll(vocab.getChiTietTuVungs());
        if (!dto.getKanjiLinks().isEmpty()) {
            AtomicInteger orderCounter = new AtomicInteger(1);
            chiTietTuVungs = dto.getKanjiLinks().stream().map(
                    kanjiLink -> {
                        ChiTietTuVung cttv = new ChiTietTuVung();
                        cttv.setTuVung(vocab);
                        cttv.setHanTu(kanjiRepository.findById(kanjiLink.getValue())
                                .orElseThrow(() -> new RuntimeException("Kanji không tồn tại với ID: " + kanjiLink.getValue())));
                        cttv.setThuTu(orderCounter.getAndIncrement());
                        return cttv;
                    }
            ).toList();
        }
        TuVung updated = vocabularyRepository.save(vocab);
        updated.setChiTietTuVungs(chiTietTuVungRepository.saveAll(chiTietTuVungs));

        return vocabToDTO(updated);
    }

    // Delete
    public void deleteVocabulary(UUID id) {
        checkAdminAccess();

        TuVung vocab = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Từ vựng không tồn tại"));

        List<CachDoc> cachDocs = readingRepository.findByLoaiDoiTuongAndMaDoiTuong("vocabulary", id);
        readingRepository.deleteAll(cachDocs);

        List<ChiTietTuVung> chiTietTuVungs = vocab.getChiTietTuVungs();
        chiTietTuVungRepository.deleteAll(chiTietTuVungs);

        vocabularyRepository.delete(vocab);
    }

    // Search
    public List<VocabularyDTOs> searchVocabulary(String keyword) {
        List<TuVung> results = vocabularyRepository.searchVocabulary(keyword); // @Query cần định nghĩa
        return results.stream().map(this::vocabToDTO).collect(Collectors.toList());
    }

    // 🔄 Entity → DTO
    private VocabularyDTOs vocabToDTO(TuVung vocab) {
        VocabularyDTOs dto = new VocabularyDTOs();
        dto.setVocalbId(vocab.getMaTuVung());
        dto.setWords(vocab.getNoiDungTu());
        dto.setMeanning(vocab.getNghia());
        dto.setWordType(vocab.getLoaiTu());
        dto.setScriptType(vocab.getKieuChu());

        if (vocab.getChiTietTuVungs() != null && !vocab.getChiTietTuVungs().isEmpty()) {
            dto.setKanjiLinks(
                    vocab.getChiTietTuVungs()
                            .stream()
                            .sorted(Comparator.comparing(ChiTietTuVung::getThuTu))
                            .map(
                                    item -> VocabularyDTOs.KanjiLink.builder()
                                            .label(String.format("%s - %s", item.getHanTu().getKyTu(), item.getHanTu().getNghia()))
                                            .value(item.getHanTu().getMaHanTu())
                                            .build())
                            .collect(Collectors.toList()));
        }
        dto.setVietnamese_pronunciation(vocab.getPhatAmTiengViet());

        List<ReadingDTO> readingDTOs = readingRepository
                .findByLoaiDoiTuongAndMaDoiTuong("vocabulary", vocab.getMaTuVung())
                .stream().map(this::readingToDTO)
                .collect(Collectors.toList());
        dto.setReadings(readingDTOs);

        return dto;
    }

    private ReadingDTO readingToDTO(CachDoc r) {
        return new ReadingDTO(
                r.getMaCachDoc(),
                r.getCachDocThucTe(),
                r.getLoaiDoc(),
                r.getLoaiDoiTuong(),
                r.getMaDoiTuong()
        );
    }

    private CachDoc readingToEntity(ReadingDTO dto) {
        CachDoc r = new CachDoc();
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
