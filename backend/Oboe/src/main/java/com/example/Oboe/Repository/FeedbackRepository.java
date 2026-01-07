package com.example.Oboe.Repository;


import com.example.Oboe.Entity.PhanHoi;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<PhanHoi, UUID> {
    @Query("SELECT f FROM PhanHoi f ORDER BY f.ngayTao DESC")
    List<PhanHoi> findTop1ByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT COUNT(f) FROM PhanHoi f")
    long countAllFeedbacks();
}
