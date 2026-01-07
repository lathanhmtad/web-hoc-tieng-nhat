package com.example.Oboe.Repository;

import com.example.Oboe.Entity.CauHoi;
import com.example.Oboe.Entity.BaiKiemTra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionsRepository extends JpaRepository<CauHoi, UUID> {
    Page<CauHoi> findByBaiKiemTra(BaiKiemTra quiz, Pageable pageable);

    int countByBaiKiemTra_MaBaiKiemTra(UUID quizzesId);
}
