package com.example.Oboe.Repository;

import com.example.Oboe.Entity.MAU_CAU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SampleSentenceRepository extends JpaRepository<MAU_CAU, UUID> {
    @Query("SELECT s FROM MAU_CAU s " +
            "WHERE LOWER(s.nghiaTiengViet) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.cauTiengNhat) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MAU_CAU> searchByVietnameseMeaning(@Param("keyword") String keyword);
}