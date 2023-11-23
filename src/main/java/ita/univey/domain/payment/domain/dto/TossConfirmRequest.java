package ita.univey.domain.payment.domain.dto;

import lombok.Data;

@Data
public class TossConfirmRequest {
    private String paymentKey;
    private String orderId;
    private int amount;
}