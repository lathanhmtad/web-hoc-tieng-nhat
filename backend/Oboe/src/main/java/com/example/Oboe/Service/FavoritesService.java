package com.example.Oboe.Service;

import com.example.Oboe.DTOs.FavoritesDTO;
import com.example.Oboe.Entity.*;
import com.example.Oboe.Repository.*;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FavoritesService {

    private final KanjiRepository kanjiRepository;
    private final UserRepository userRepository;
    private final FavoritesRepository favoritesRepository;
    private final GrammarRepository grammarRepository;
    private final VocabularyRepository vocabularyRepository;
    private final SampleSentenceRepository sampleSentenceRepository;

    public FavoritesService(KanjiRepository kanjiRepository,
                            UserRepository userRepository,
                            FavoritesRepository favoritesRepository,
                            GrammarRepository grammarRepository,
                            VocabularyRepository vocabularyRepository, SampleSentenceRepository sampleSentenceRepository
                           ) {
        this.kanjiRepository = kanjiRepository;
        this.userRepository = userRepository;
        this.favoritesRepository = favoritesRepository;
        this.grammarRepository = grammarRepository;
        this.vocabularyRepository = vocabularyRepository;
        this.sampleSentenceRepository = sampleSentenceRepository;
    }
    public FavoritesDTO toggleFavorite(FavoritesDTO dto, UUID userId) {
        YEU_THICH existing = null;
        // Kiểm tra xem nội dung yêu thích là loại nào (Kanji, Grammar, Vocab, hoặc Sentence)
        // Và tìm trong database xem người dùng đã từng thích mục đó chưa.
        if (dto.getKanjiId() != null) {
            // Nếu là Kanji tìm trong bảng favorites theo userId + kanjiId
            existing = favoritesRepository.findFavoriteKanji(userId, dto.getKanjiId()).orElse(null);
        } else if (dto.getGrammaId() != null) {
            // Nếu là Grammar
            existing = favoritesRepository.findFavoriteGramma(userId, dto.getGrammaId()).orElse(null);
        } else if (dto.getVocabularyId() != null) {
            // Nếu là  (Vocabulary)
            existing = favoritesRepository.findFavoriteVocabulary(userId, dto.getVocabularyId()).orElse(null);
        } else if (dto.getSampleSentenceId() != null) {
            // Nếu là  (Sample Sentence)
            existing = favoritesRepository.findFavoriteSentence(userId, dto.getSampleSentenceId()).orElse(null);
        } else {
            // Không truyền loại nội dung nào → lỗi
            throw new RuntimeException("Phải cung cấp ít nhất 1 loại nội dung.");
        }
        // Nếu người dùng đã từng yêu thích nội dung này → xóa (bỏ yêu thích)
        if (existing != null) {
            favoritesRepository.delete(existing);
            return null; // Trả null nghĩa là đã bỏ yêu thích
        }
        // Nếu chưa từng yêu thích → tạo mới mục yêu thích
        return createFavorite(dto, userId);
    }
    public boolean isFavorited(UUID userId, String type, UUID targetId) {
        return switch (type) {
            case "kanji" -> favoritesRepository.findFavoriteKanji(userId, targetId).isPresent();
            case "grammar" -> favoritesRepository.findFavoriteGramma(userId, targetId).isPresent();
            case "vocabulary" -> favoritesRepository.findFavoriteVocabulary(userId, targetId).isPresent();
            case "samplesentence" -> favoritesRepository.findFavoriteSentence(userId, targetId).isPresent();
            default -> throw new IllegalArgumentException("Loại nội dung không hợp lệ: " + type);
        };
    }

    public FavoritesDTO createFavorite(FavoritesDTO dto, UUID userId) {
        YEU_THICH yeuThich = new YEU_THICH();

        yeuThich.setNgayTao(dto.getFavoritesAt() != null ? dto.getFavoritesAt() : java.time.LocalDate.now());

        NGUOI_DUNG nguoiDung = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        yeuThich.setNguoiDung(nguoiDung);

        if (dto.getKanjiId() != null) {
            HAN_TU hanTu = kanjiRepository.findById(dto.getKanjiId())
                    .orElseThrow(() -> new RuntimeException("Kanji not found"));
            yeuThich.setHanTu(hanTu);
            yeuThich.setTieuDe(hanTu.getKyTu());
            yeuThich.setNoiDung(hanTu.getNghia());
        } else if (dto.getGrammaId() != null) {
            NGU_PHAP nguPhap = grammarRepository.findById(dto.getGrammaId())
                    .orElseThrow(() -> new RuntimeException("Grammar not found"));
            yeuThich.setNguPhap(nguPhap);
            yeuThich.setTieuDe(nguPhap.getCauTruc());
            yeuThich.setNoiDung(nguPhap.getGiaiThich());
        }  else if (dto.getVocabularyId() != null) {
        TU_VUNG tuVung = vocabularyRepository.findById(dto.getVocabularyId())
                .orElseThrow(() -> new RuntimeException("Vocabulary not found"));
            yeuThich.setTuVung(tuVung);
            yeuThich.setTieuDe(tuVung.getNoiDungTu());
            yeuThich.setNoiDung(tuVung.getNghia());
        }
        else if (dto.getSampleSentenceId() != null)  {
                MAU_CAU mauCau = sampleSentenceRepository.findById(dto.getSampleSentenceId())
                        .orElseThrow(() -> new RuntimeException("SampleSentence not found"));
            yeuThich.setMauCau(mauCau);
            yeuThich.setTieuDe(mauCau.getCauTiengNhat());
            yeuThich.setNoiDung(mauCau.getNghiaTiengViet());
        }

        else {
            throw new RuntimeException("Phải cung cấp ít nhất 1 loại nội dung yêu thích (Kanji, Grammar, ...).");
        }

        favoritesRepository.save(yeuThich);
        return toDTO(yeuThich);
    }

    //lấy tất các từ yêu thích theo type
    public List<FavoritesDTO> getFavoritesByUserIdAndType(UUID userId, String type) {
        List<YEU_THICH> list = favoritesRepository.findByUserId(userId);

        return list.stream()
                .filter(fav -> {
                    return switch (type.toLowerCase()) {
                        case "kanji" -> fav.getHanTu() != null;
                        case "grammar" -> fav.getNguPhap() != null;
                        case "vocabulary" -> fav.getTuVung() != null;
                        case "samplesentence" -> fav.getMauCau() != null;
                        default -> false;
                    };
                })
                .map(fav -> {
                    FavoritesDTO dto = toDTO(fav);
                    dto.setType(type.toLowerCase());
                    return dto;
                })
                .toList();
    }

    //lấy tất cả các tử yêu thích
    public List<FavoritesDTO> getAllFavoritesByUserId(UUID userId) {
        List<YEU_THICH> list = favoritesRepository.findByUserId(userId);

        return list.stream()
                .map(fav -> {
                    FavoritesDTO dto = toDTO(fav);

                    if (fav.getHanTu() != null) {
                        dto.setType("kanji");
                    } else if (fav.getNguPhap() != null) {
                        dto.setType("grammar");
                    } else if (fav.getTuVung() != null) {
                        dto.setType("vocabulary");
                    } else if (fav.getMauCau() != null) {
                        dto.setType("samplesentence");
                    } else {
                        dto.setType("unknown");
                    }

                    return dto;
                })
                .toList();
    }
    // Xóa mục yêu thích dựa trên ID và kiểm tra quyền sở hữu của người dùng
    public void deleteFavorite(UUID favoriteId, UUID userId) {
        // Tìm mục yêu thích theo ID, nếu không tồn tại thì ném ra lỗi
        YEU_THICH favorite = favoritesRepository.findById(favoriteId)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));

        // Kiểm tra xem mục yêu thích này có thuộc về người dùng hiện tại không
        if (!favorite.getNguoiDung().getMaNguoiDung().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa mục yêu thích này");
        }

        // Thực hiện xóa mục yêu thích khỏi cơ sở dữ liệu
        favoritesRepository.delete(favorite);
    }


    public static FavoritesDTO toDTO(YEU_THICH yeuThich) {
        if (yeuThich == null) return null;
        FavoritesDTO dto = new FavoritesDTO(
                yeuThich.getMaYeuThich(),
                yeuThich.getTieuDe(),
                yeuThich.getNoiDung(),
                yeuThich.getNgayTao(),
                yeuThich.getNguoiDung() != null ? yeuThich.getNguoiDung().getMaNguoiDung() : null,
                yeuThich.getNguPhap() != null ? yeuThich.getNguPhap().getMaNguPhap() : null,
                yeuThich.getHanTu() != null ? yeuThich.getHanTu().getMaHanTu() : null,
                yeuThich.getMauCau() != null ? yeuThich.getMauCau().getMaMauCau() : null,
                yeuThich.getTuVung() != null ? yeuThich.getTuVung().getMaTuVung() : null
        );
        // Xác định type
        if (yeuThich.getHanTu() != null) {
            dto.setType("kanji");
        } else if (yeuThich.getNguPhap() != null) {
            dto.setType("grammar");
        } else if (yeuThich.getTuVung() != null) {
            dto.setType("vocabulary");
        } else if (yeuThich.getMauCau() != null) {
            dto.setType("samplesentence");
        } else {
            dto.setType("unknown");
        }
        return dto;
    }

}

