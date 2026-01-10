package com.example.Oboe.Repository;


import com.example.Oboe.Entity.PHAN_HOI;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<PHAN_HOI, UUID> {
    @Query("SELECT f FROM PHAN_HOI f ORDER BY f.ngayTao DESC")
    List<PHAN_HOI> findTop1ByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT COUNT(f) FROM PHAN_HOI f")
    long countAllFeedbacks();
}
