package com.example.Oboe.Repository;

import com.example.Oboe.Entity.CAU_HOI;
import com.example.Oboe.Entity.BAI_KIEM_TRA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionsRepository extends JpaRepository<CAU_HOI, UUID> {
    Page<CAU_HOI> findByBaiKiemTra(BAI_KIEM_TRA quiz, Pageable pageable);
    List<CAU_HOI> findByBaiKiemTra_MaBaiKiemTra(UUID quiz);

    int countByBaiKiemTra_MaBaiKiemTra(UUID quizzesId);
}
