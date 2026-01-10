package com.example.Oboe.Repository;

import com.example.Oboe.Entity.NGU_PHAP;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GrammarRepository extends JpaRepository<NGU_PHAP, UUID> {
    @Query("SELECT g FROM NGU_PHAP g WHERE LOWER(g.cauTruc) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(g.giaiThich) LIKE LOWER(CONCAT('%', :keyword, '%')) ")
    List<NGU_PHAP> searchGrammar(@Param("keyword") String keyword);

    @Query("SELECT g FROM NGU_PHAP g WHERE LOWER(g.cauTruc) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(g.giaiThich) LIKE LOWER(CONCAT('%', :keyword, '%')) ")
    Page<NGU_PHAP> getALl(@Param("keyword") String keyword, Pageable pageable);
}
