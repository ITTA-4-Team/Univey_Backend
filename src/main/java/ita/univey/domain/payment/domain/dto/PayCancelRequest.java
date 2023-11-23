package ita.univey.domain.payment.domain.dto;

import lombok.Data;

@Data
public class PayCancelRequest {
    private String paymentKey;

    private String cancelReason;
    private int cancelAmount;

    private String refundReceiveAccount ;

    private int taxFreeAmount;
    private String currency;

}