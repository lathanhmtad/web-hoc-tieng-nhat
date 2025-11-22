package com.example.Oboe.Service;

import com.example.Oboe.DTOs.SampleSentenceDTO;
import com.example.Oboe.Entity.MauCau;
import com.example.Oboe.Repository.SampleSentenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SampleSentenceServiceImpl implements SampleSentenceService {

    @Autowired
    private SampleSentenceRepository repository;

    private SampleSentenceDTO convertToDTO(MauCau entity) {
        return new SampleSentenceDTO(
                entity.getSample_sentence_id(),
                entity.getJapaneseText(),
                entity.getVietnameseMeaning()
        );
    }

    private MauCau convertToEntity(SampleSentenceDTO dto) {
        MauCau entity = new MauCau();
        entity.setSample_sentence_id(dto.getId());
        entity.setJapaneseText(dto.getJapaneseText());
        entity.setVietnameseMeaning(dto.getVietnameseMeaning());
        return entity;
    }

    @Override
    public SampleSentenceDTO create(SampleSentenceDTO dto) {
        MauCau entity = convertToEntity(dto);
        return convertToDTO(repository.save(entity));
    }

    @Override
    public SampleSentenceDTO update(UUID id, SampleSentenceDTO dto) {
        MauCau entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SampleSentence not found"));
        entity.setJapaneseText(dto.getJapaneseText());
        entity.setVietnameseMeaning(dto.getVietnameseMeaning());
        return convertToDTO(repository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public SampleSentenceDTO getById(UUID id) {
        MauCau entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SampleSentence not found"));
        return convertToDTO(entity);
    }

    @Override
    public Map<String, Object> getAll(Pageable pageable) {
        Page<MauCau> pageResult = repository.findAll(pageable);
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
