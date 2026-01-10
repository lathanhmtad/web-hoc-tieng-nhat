package com.example.Oboe.Service;

import com.example.Oboe.Entity.*;
import com.example.Oboe.Repository.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class SearchService {

    private KanjiRepository kanjiRepository;
    private VocabularyRepository vocabularyRepository;
    private GrammarRepository grammarRepository;
    private SampleSentenceRepository sampleSentenceRepository;

    // Gợi ý tất cả các loại học liệu (cho /suggest)
    public List<Map<String, String>> suggestAllTypes(String keyword) {
        List<Map<String, String>> suggestions = new ArrayList<>();

        suggestions.addAll(searchByType(keyword, "vocabulary"));
        suggestions.addAll(searchByType(keyword, "kanji"));
        suggestions.addAll(searchByType(keyword, "grammar"));
        suggestions.addAll(searchByType(keyword, "sentence"));

        return suggestions;
    }

    // Tìm theo type cụ thể (học liệu)
    public List<Map<String, String>> searchByType(String keyword, String type) {
        List<Map<String, String>> suggestions = new ArrayList<>();

        switch (type.toLowerCase()) {
            case "vocabulary":
                for (TU_VUNG v : vocabularyRepository.searchVocabulary(keyword)) {
                    Map<String, String> item = new HashMap<>();
                    item.put("type", "vocabulary");
                    item.put("word", v.getNoiDungTu());
                    item.put("reading", v.getPhatAmTiengViet());
                    item.put("meaning", v.getNghia());
                    item.put("id", v.getMaTuVung().toString());
                    suggestions.add(item);
                }
                break;

            case "kanji":
                for (HAN_TU k : kanjiRepository.searchKanji(keyword)) {
                    Map<String, String> item = new HashMap<>();
                    item.put("type", "kanji");
                    item.put("word", k.getKyTu());
                    item.put("reading", k.getPhatAmTiengViet());
                    item.put("meaning", k.getNghia());
                    item.put("id", k.getMaHanTu().toString());
                    suggestions.add(item);
                }
                break;

            case "grammar":
                for (NGU_PHAP g : grammarRepository.searchGrammar(keyword)) {
                    Map<String, String> item = new HashMap<>();
                    item.put("type", "grammar");
                    item.put("word", g.getCauTruc());
//                    item.put("reading", g.getPhatAmTiengViet());
                    item.put("meaning", g.getGiaiThich());
                    item.put("id", g.getMaNguPhap().toString());
                    suggestions.add(item);
                }
                break;

            case "sentence":
                for (MAU_CAU s : sampleSentenceRepository.searchByVietnameseMeaning(keyword)) {
                    Map<String, String> item = new HashMap<>();
                    item.put("type", "sentence");
                    item.put("word", s.getCauTiengNhat());
                    //item.put("reading", s.getCachDocRomaji());
                    item.put("meaning", s.getNghiaTiengViet());
                    item.put("id", s.getMaMauCau().toString());
                    suggestions.add(item);
                }
                break;

            default:
                throw new IllegalArgumentException("Type không hợp lệ: " + type);
        }

        return suggestions;
    }


}
