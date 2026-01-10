package com.example.Oboe.Repository;

import com.example.Oboe.Entity.AI_PHAN_HOI_BAI_VIET;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AIBlogReplyRepository extends JpaRepository<AI_PHAN_HOI_BAI_VIET, UUID> {
    AI_PHAN_HOI_BAI_VIET findByBaiViet_maBaiViet(UUID blogId);
}
