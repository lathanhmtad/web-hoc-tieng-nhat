package com.example.Oboe.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
public class PayOsBeanConfig {

    private final PayOsConfig payOsConfig;

    public PayOsBeanConfig(PayOsConfig payOsConfig) {
        this.payOsConfig = payOsConfig;
    }

    @Bean
    public PayOS payOS() {
        return new PayOS(
                payOsConfig.getClientId().trim(),
                payOsConfig.getApiKey().trim(),
                payOsConfig.getChecksumKey().trim() // Có thể null nếu không cần
        );
    }
}
