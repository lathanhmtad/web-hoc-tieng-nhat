package com.example.Oboe.Repository;

import com.example.Oboe.Entity.KetQua;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuizResultRepository extends JpaRepository<KetQua, UUID> {

}

