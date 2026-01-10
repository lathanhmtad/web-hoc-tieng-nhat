package com.example.Oboe.Service;

import com.example.Oboe.DTOs.SampleSentenceDTO;
import com.example.Oboe.Entity.MAU_CAU;
import com.example.Oboe.Repository.GrammarRepository;
import com.example.Oboe.Repository.SampleSentenceRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Service
public class SampleSentenceServiceImpl implements SampleSentenceService {

    private SampleSentenceRepository repository;
    private GrammarRepository grammarRepository;

    private SampleSentenceDTO convertToDTO(MAU_CAU entity) {
        return SampleSentenceDTO.builder()
                .id(entity.getMaMauCau())
                .japaneseText(entity.getCauTiengNhat())
                .vietnameseMeaning(entity.getNghiaTiengViet())
                .reading("")
                .grammarLink(
                        SampleSentenceDTO.GrammarLink.builder()
                                .label(entity.getNguPhap() != null ? entity.getNguPhap().getCauTruc() : null)
                                .value(entity.getNguPhap() != null ? entity.getNguPhap().getMaNguPhap() : null)
                                .build()
                )
                .build();
    }

    private MAU_CAU convertToEntity(SampleSentenceDTO dto) {
        MAU_CAU entity = new MAU_CAU();
        entity.setMaMauCau(dto.getId());
        entity.setCauTiengNhat(dto.getJapaneseText());
        entity.setNghiaTiengViet(dto.getVietnameseMeaning());
        entity.setNguPhap(
                grammarRepository
                        .findById(dto
                                .getGrammarLink()
                                .getValue())
                        .orElse(null));
        return entity;
    }

    @Override
    public SampleSentenceDTO create(SampleSentenceDTO dto) {
        MAU_CAU entity = convertToEntity(dto);
        return convertToDTO(repository.save(entity));
    }

    @Override
    public SampleSentenceDTO update(UUID id, SampleSentenceDTO dto) {
        MAU_CAU entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SampleSentence not found"));
        entity.setCauTiengNhat(dto.getJapaneseText());
        entity.setNghiaTiengViet(dto.getVietnameseMeaning());

        if (dto.getGrammarLink().getValue() != null) {
            entity.setNguPhap(
                    grammarRepository
                            .findById(dto.getGrammarLink().getValue())
                            .orElse(null));
        }

        return convertToDTO(repository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public SampleSentenceDTO getById(UUID id) {
        MAU_CAU entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SampleSentence not found"));
        return convertToDTO(entity);
    }

    @Override
    public Map<String, Object> getAll(Pageable pageable) {
        Page<MAU_CAU> pageResult = repository.findAll(pageable);
        Page<SampleSentenceDTO> dtoPage = pageResult.map(this::convertToDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("content", dtoPage.getContent());
        response.put("currentPage", dtoPage.getNumber());
        response.put("totalItems", dtoPage.getTotalElements());
        response.put("totalPages", dtoPage.getTotalPages());
        response.put("isLastPage", dtoPage.isLast());

        return response;
    }
}
