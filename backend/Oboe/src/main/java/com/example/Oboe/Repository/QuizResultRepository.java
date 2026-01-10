package com.example.Oboe.Repository;

import com.example.Oboe.Entity.LICH_SU_LAM_BAI;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuizResultRepository extends JpaRepository<LICH_SU_LAM_BAI, UUID> {

}

