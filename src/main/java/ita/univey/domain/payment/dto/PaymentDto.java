package ita.univey.domain.payment.dto;

import ita.univey.domain.payment.entity.PayType;
import ita.univey.domain.payment.entity.Payment;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

//아래 값들을 처음에 프론트에서 받음
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

    @NotNull
    private PayType payType; //결제 타입 - 카드/현금/포인트

    @NotNull
    private Integer amount; //가격 정보

    @NotNull
    private String orderName; //주문명

    private String successUrl; //성공 시 리다이렉트 될 URL
    private String failUrl; //실패 시 리다이렉트 될 URL

    public Payment toEntity() { //entity로 바꾸는 메소드
        return Payment.builder()
                .payType(payType)
                .amount(amount)
                .orderName(orderName)
                .orderId(UUID.randomUUID().toString())
                .paySuccessYN(false)
                .build();
    }
}
