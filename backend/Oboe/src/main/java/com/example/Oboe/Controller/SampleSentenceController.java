package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.SampleSentenceDTO;
import com.example.Oboe.Service.SampleSentenceService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/sample-sentences")
public class SampleSentenceController {

    private SampleSentenceService service;

    @GetMapping("/{id}")
    public SampleSentenceDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    public Map<String, Object> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return service.getAll(pageable);
    }

    @PostMapping
    public SampleSentenceDTO create(@RequestBody SampleSentenceDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public SampleSentenceDTO update(@PathVariable UUID id, @RequestBody SampleSentenceDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(1);
    }
}
