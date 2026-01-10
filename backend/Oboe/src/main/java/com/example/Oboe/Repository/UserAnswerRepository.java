package com.example.Oboe.Repository;

import com.example.Oboe.Entity.CHI_TIET_BAI_LAM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserAnswerRepository extends JpaRepository<CHI_TIET_BAI_LAM, UUID> {

}
