package com.example.Oboe.Repository;

import com.example.Oboe.Entity.XuLyBaoCao;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ReportActionsRepository extends JpaRepository<XuLyBaoCao, UUID> {
    @Modifying
    @Transactional
    @Query("delete from XuLyBaoCao ra where ra.baoCao.maBaoCao = :reportID")
    void deleteByReportId(UUID reportID);
}
