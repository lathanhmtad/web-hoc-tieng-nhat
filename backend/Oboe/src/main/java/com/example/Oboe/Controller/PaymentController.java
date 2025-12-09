package com.example.Oboe.Controller;
import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.Entity.DonHang;
import com.example.Oboe.Repository.PaymentRepository;
import com.example.Oboe.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import com.example.Oboe.Service.MomoService;
import com.example.Oboe.Service.PayOsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
@AllArgsConstructor
public class PaymentController {
    private PayOsService payOsService;
    private UserRepository userRepository;
    private MomoService momoService;
    private PaymentRepository paymentRepository;

    @PostMapping("/momo")
    public ResponseEntity<?> payWithMomo(@RequestParam UUID userId) {
        try {
            Map<String, String> paymentResult = momoService.createPayment(userId);
            return ResponseEntity.ok(paymentResult); // Trả về full thông tin: payUrl, orderId, requestId
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Lỗi tạo thanh toán",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/momo-notify")
    public ResponseEntity<String> handleMomoCallback(@RequestBody Map<String, String> payload) {
        momoService.handleMomoCallback(payload);
        return ResponseEntity.ok("success");
    }


    @PostMapping("/payos")
    public ResponseEntity<?> payWithPayOS(@RequestParam(required = false) Integer amount,
                                          Authentication authentication) {
        try {
            CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
            UUID userId = customUser.getUserID();

            var result = payOsService.createPayment(99000, "Thanh toán Oboeru", userId);

            if (result == null || result.getCheckoutUrl() == null) {
                return ResponseEntity.status(500).body(Map.of(
                        "error", "Lỗi tạo thanh toán PayOS",
                        "message", "Không có dữ liệu trả về từ PayOS hoặc thiếu checkoutUrl"
                ));
            }

            // Lấy lại payment từ orderCode
            long orderCode = result.getOrderCode();
            DonHang donHang = paymentRepository.findByMaDonHang(orderCode);
            if (donHang == null) {
                return ResponseEntity.status(500).body(Map.of("error", "Không tìm thấy đơn hàng vừa tạo"));
            }

            String encodedCheckoutUrl = URLEncoder.encode(result.getCheckoutUrl(), StandardCharsets.UTF_8);
//            String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=" + result.getCheckoutUrl();

            String qrUrl = result.getQrCode();

            Map<String, Object> body = new HashMap<>();
            body.put("amount", donHang.getSoTien());
            body.put("orderCode", donHang.getMaDonHang());
            body.put("status", donHang.getTrangThai());
            body.put("paid_at", donHang.getThoiGianThanhToan());
            body.put("qrUrl", qrUrl);
            body.put("checkoutUrl", result.getCheckoutUrl());

            System.out.println("Checkout result: " + result);
            return ResponseEntity.ok(body);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Lỗi PayOS",
                    "message", e.getMessage()
            ));
        }
    }


    @PostMapping("/payos-notify")
    public ResponseEntity<?> handlePayOsCallback(@RequestBody String rawJson) {
        try {
            System.out.println("Webhook Received:\n" + rawJson);

            var data = payOsService.handleWebhook(rawJson);

            System.out.println("Payment trangThai updated for orderCode: " + data.getOrderCode() +
                    ", trangThai code: " + data.getCode());


            return ResponseEntity.ok("Received");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed: " + e.getMessage());
        }
    }


    @GetMapping("/payment-success")
    public ResponseEntity<?> handlePaymentSuccess(
            @RequestParam String code,
            @RequestParam String cancel,
            @RequestParam String status,
            @RequestParam String orderCode) {
        return ResponseEntity.ok(Map.of(
                "message", "Cảm ơn bạn đã thanh toán! Hệ thống sẽ tự động xác nhận.",
                "orderCode", orderCode,
                "status", status,
                "cancel", cancel
        ));
    }

    @GetMapping("/payment-cancel")
    public ResponseEntity<?> handleCancel(@RequestParam(required = false) String orderCode) {
        if (orderCode == null || orderCode.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Thiếu orderCode"));
        }

        try {
            Long orderCodeLong = Long.parseLong(orderCode);
            DonHang donHang = paymentRepository.findByMaDonHang(orderCodeLong);

            if (donHang != null) {
                donHang.setTrangThai("CANCELLED");
                paymentRepository.save(donHang);
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Thanh toán đã bị hủy",
                    "orderCode", orderCodeLong
            ));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "orderCode không hợp lệ"));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getPaymentStatus(@RequestParam Long orderCode) {
        DonHang donHang = paymentRepository.findByMaDonHang(orderCode);
        if (donHang == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Không tìm thấy đơn hàng"));
        }

        return ResponseEntity.ok(Map.of(
                "orderCode", donHang.getMaDonHang(),
                "status", donHang.getTrangThai(),
                "amount", donHang.getSoTien(),
                "user", donHang.getNguoiDung().getMaNguoiDung()
        ));
    }
}
