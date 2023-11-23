package ita.univey.domain.payment.domain.dto;

import ita.univey.domain.payment.domain.entity.PayType;
import ita.univey.domain.payment.domain.entity.Payment;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

//처음 프론트에서 입력받음
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

    @NotNull
    private PayType payType;

    @NotNull
    private Long amount;

    @NotNull
    private String orderName;

    @NotNull
    private String SuccessUrl;

    @NotNull
    private String failUrl;

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
