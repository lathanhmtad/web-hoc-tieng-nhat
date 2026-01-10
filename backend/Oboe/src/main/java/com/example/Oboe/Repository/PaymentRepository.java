package com.example.Oboe.Repository;

import com.example.Oboe.Entity.DON_HANG;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<DON_HANG, UUID> {
    DON_HANG findByMaDonHang(Long orderCode);
}

