package com.example.Oboe.Repository;

import com.example.Oboe.Entity.CachDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;



public interface ReadingRepository extends JpaRepository<CachDoc, UUID> {

    List<CachDoc> findByLoaiDoiTuongAndMaDoiTuong(String ownerType, UUID ownerId);
    @Query("""
    SELECT r FROM CachDoc r
    WHERE LOWER(r.cachDocThucTe) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<CachDoc> searchReadingsByText(@Param("keyword") String keyword);



}

