package com.example.Oboe.Service;

import com.example.Oboe.Config.PayOsConfig;
import com.example.Oboe.Entity.*;
import com.example.Oboe.Repository.PaymentRepository;
import com.example.Oboe.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.WebhookData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PayOsService {

    private final PayOS payOS;
    private final PayOsConfig config;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public PayOsService(PayOS payOS,
                        PayOsConfig config,
                        PaymentRepository paymentRepository,
                        UserRepository userRepository) {
        this.payOS = payOS;
        this.config = config;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    public CheckoutResponseData createPayment(BigDecimal amount, String itemName, UUID userId) throws Exception {

        ItemData item = ItemData.builder()
                .name(itemName)
                .quantity(1)
                .price(amount.intValue())
                .build();

        long tempOrderCode = System.currentTimeMillis();

        long expiredAt = (System.currentTimeMillis() / 1000) + (15 * 60);

        PaymentData paymentData = PaymentData.builder()
                .orderCode(tempOrderCode)
                .amount(amount.intValue())
                .description("Pay Oboeru " + tempOrderCode)
                .returnUrl(config.getReturnUrl())
                .cancelUrl(config.getCancelUrl())
                .items(List.of(item))
                .expiredAt(expiredAt)
                .build();

        CheckoutResponseData response = payOS.createPaymentLink(paymentData);

        // Save to DB
        DON_HANG donHang = new DON_HANG();
        donHang.setMaDonHang(tempOrderCode);
        donHang.setSoTien(amount);
        donHang.setNguoiDung(userRepository.findById(userId).orElse(null));
        donHang.setTrangThai(TRANG_THAI_THANH_TOAN.PENDING);
        donHang.setPhuongThucThanhToan(PHUONG_THUC_THANH_TOAN.PAYOS);
        paymentRepository.save(donHang);

        return response;
    }

    public WebhookData handleWebhook(String rawJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        vn.payos.type.Webhook webhook = mapper.readValue(rawJson, vn.payos.type.Webhook.class);
        WebhookData data = payOS.verifyPaymentWebhookData(webhook);

        long orderCode = data.getOrderCode();
        String code = data.getCode();

        DON_HANG donHang = paymentRepository.findByMaDonHang(orderCode);
        if (donHang != null) {
            TRANG_THAI_THANH_TOAN status;

            switch (code) {
                case "00" -> {
                    status = TRANG_THAI_THANH_TOAN.SUCCESS;
                    donHang.setThoiGianThanhToan(LocalDateTime.now());
                    donHang.setNgayHetHan(LocalDateTime.now().plusMonths(1));
                    NGUOI_DUNG nguoiDung = donHang.getNguoiDung();
                    if (nguoiDung.getLoaiTaiKhoan() != LOAI_TAI_KHOAN.PREMIUM) {
                        nguoiDung.setLoaiTaiKhoan(LOAI_TAI_KHOAN.PREMIUM);
                        userRepository.save(nguoiDung);
                    }
                }
                case "09" -> status = TRANG_THAI_THANH_TOAN.CANCELLED;
                default -> status = TRANG_THAI_THANH_TOAN.FAILED;
            }

            donHang.setTrangThai(status);
            paymentRepository.save(donHang);
        }

        return data;
    }

    public void cancelPayment(long orderCode, String reason) throws Exception {
        payOS.cancelPaymentLink(orderCode, reason);
    }

    public Object getPaymentInfo(long orderCode) throws Exception {
        return payOS.getPaymentLinkInformation(orderCode);
    }
}
