package com.example.Oboe.Repository;

import com.example.Oboe.Entity.NguPhap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GrammarRepository extends JpaRepository<NguPhap, UUID> {
    @Query("SELECT g FROM NguPhap g WHERE LOWER(g.cauTruc) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(g.giaiThich) LIKE LOWER(CONCAT('%', :keyword, '%')) ")
    List<NguPhap> searchGrammar(@Param("keyword") String keyword);

    @Query("SELECT g FROM NguPhap g WHERE LOWER(g.cauTruc) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(g.giaiThich) LIKE LOWER(CONCAT('%', :keyword, '%')) ")
    Page<NguPhap> getALl(@Param("keyword") String keyword, Pageable pageable);
}
