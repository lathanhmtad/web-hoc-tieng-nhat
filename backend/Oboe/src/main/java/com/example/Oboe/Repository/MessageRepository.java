package com.example.Oboe.Repository;

import com.example.Oboe.Entity.TinNhan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<TinNhan, UUID> {

    @Query(value = """
    SELECT DISTINCT 
        CASE 
            WHEN m.nguoiGui.maNguoiDung = :userId THEN m.nguoiNhan.maNguoiDung
            ELSE m.nguoiGui.maNguoiDung
        END
    FROM TinNhan m
    WHERE m.nguoiGui.maNguoiDung = :userId OR m.nguoiNhan.maNguoiDung = :userId
    """)
    List<UUID> findAllPartnerIds(@Param("userId") UUID userId);

    @Query("SELECT m FROM TinNhan m WHERE " +
            "(m.nguoiGui.maNguoiDung = :userA AND m.nguoiNhan.maNguoiDung = :userB) OR " +
            "(m.nguoiGui.maNguoiDung = :userB AND m.nguoiNhan.maNguoiDung = :userA) " +
            "ORDER BY m.thoiGianGui DESC")
    List<TinNhan> findConversation(@Param("userA") UUID userA,
                                   @Param("userB") UUID userB,
                                   org.springframework.data.domain.Pageable pageable);
    @Query("SELECT m FROM TinNhan m WHERE " +
            "(m.nguoiGui.maNguoiDung = :userA AND m.nguoiNhan.maNguoiDung = :userB) OR " +
            "(m.nguoiGui.maNguoiDung = :userB AND m.nguoiNhan.maNguoiDung = :userA) " +
            "ORDER BY m.thoiGianGui DESC")
    List<TinNhan> findMessageNew(@Param("userA") UUID userA,
                                 @Param("userB") UUID userB,
                                 Pageable pageable);





}
