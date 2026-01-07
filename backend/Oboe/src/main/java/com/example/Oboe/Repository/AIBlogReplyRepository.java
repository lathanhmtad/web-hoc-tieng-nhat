package com.example.Oboe.Repository;

import com.example.Oboe.Entity.AIPhanHoiBaiViet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AIBlogReplyRepository extends JpaRepository<AIPhanHoiBaiViet, UUID> {
    AIPhanHoiBaiViet findByBaiViet_maBaiViet(UUID blogId);
}
