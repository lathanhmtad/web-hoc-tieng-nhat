package com.example.Oboe.Repository;

import com.example.Oboe.Entity.THONG_BAO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.UUID;

public interface NotificationsRepository extends JpaRepository<THONG_BAO, UUID> {

    @Query("SELECT n FROM THONG_BAO n WHERE n.nguoiDung.maNguoiDung = :maNguoiDung ORDER BY n.thoiGianCapNhat DESC")
    List<THONG_BAO> findConversation(@Param("maNguoiDung") UUID maNguoiDung, Pageable pageable);
    @Modifying
    @Query("UPDATE THONG_BAO n SET n.daDuocDoc = true WHERE n.nguoiDung.maNguoiDung = :userId AND n.daDuocDoc = false")
    int markAllAsRead(@Param("userId") UUID userId);


}


