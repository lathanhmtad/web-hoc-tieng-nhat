package com.example.Oboe.Repository;

import com.example.Oboe.Entity.CACH_DOC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;



public interface ReadingRepository extends JpaRepository<CACH_DOC, UUID> {

    List<CACH_DOC> findByLoaiDoiTuongAndMaDoiTuong(String ownerType, UUID ownerId);
    @Query("""
    SELECT r FROM CACH_DOC r
    WHERE LOWER(r.cachDocThucTe) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<CACH_DOC> searchReadingsByText(@Param("keyword") String keyword);



}

