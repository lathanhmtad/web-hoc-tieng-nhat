package com.example.Oboe.Repository;

import com.example.Oboe.Entity.ThongBao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.UUID;

public interface NotificationsRepository extends JpaRepository<ThongBao, UUID> {

    @Query("SELECT n FROM ThongBao n WHERE n.nguoiDung.user_id = :user_Id ORDER BY n.update_at DESC")
    List<ThongBao> findConversation(@Param("user_Id") UUID user_id, Pageable pageable);
    @Modifying
    @Query("UPDATE ThongBao n SET n.isRead = true WHERE n.nguoiDung.user_id = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") UUID userId);


}


