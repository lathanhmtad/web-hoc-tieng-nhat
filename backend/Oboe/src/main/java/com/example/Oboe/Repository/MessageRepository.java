package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query(value = """
    SELECT DISTINCT 
        CASE 
            WHEN m.sender.maNguoiDung = :userId THEN m.receiver.maNguoiDung
            ELSE m.sender.maNguoiDung
        END
    FROM Message m
    WHERE m.sender.maNguoiDung = :userId OR m.receiver.maNguoiDung = :userId
    """)
    List<UUID> findAllPartnerIds(@Param("userId") UUID userId);

    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.maNguoiDung = :userA AND m.receiver.maNguoiDung = :userB) OR " +
            "(m.sender.maNguoiDung = :userB AND m.receiver.maNguoiDung = :userA) " +
            "ORDER BY m.sent_at DESC")
    List<Message> findConversation(@Param("userA") UUID userA,
                                         @Param("userB") UUID userB,
                                         org.springframework.data.domain.Pageable pageable);
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.maNguoiDung = :userA AND m.receiver.maNguoiDung = :userB) OR " +
            "(m.sender.maNguoiDung = :userB AND m.receiver.maNguoiDung = :userA) " +
            "ORDER BY m.sent_at DESC")
    List<Message> findMessageNew(@Param("userA") UUID userA,
                                           @Param("userB") UUID userB,
                                           Pageable pageable);





}
