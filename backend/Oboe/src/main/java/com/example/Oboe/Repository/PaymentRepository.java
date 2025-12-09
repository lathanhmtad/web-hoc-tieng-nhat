package com.example.Oboe.Repository;

import com.example.Oboe.Entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<DonHang, UUID> {
    DonHang findByMaGiaoDich(String transactionId);
    DonHang findByMaDonHang(Long orderCode);
}

