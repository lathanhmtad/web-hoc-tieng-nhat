package com.example.Oboe.Repository;

import com.example.Oboe.Entity.XU_LY_BAO_CAO;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ReportActionsRepository extends JpaRepository<XU_LY_BAO_CAO, UUID> {
    @Modifying
    @Transactional
    @Query("delete from XU_LY_BAO_CAO ra where ra.baoCao.maBaoCao = :reportID")
    void deleteByReportId(UUID reportID);
}
