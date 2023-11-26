package ita.univey.domain.payment.domain.dto;

import lombok.*;

//paymentDto로 받은 정보들 검증 후, 실제 토스페이먼츠에서 결제 요청을 하기 위한 값들을 포함해서 PaymentResDto로 반환
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResDto {

    private String payType;
    private Long amount;
    private String orderName;
    private String orderId;
    private String userEmail;
    private String userName;
    private String successUrl;
    private String failUrl;
    private String failReason;
    private boolean cancelYN;
    private String createdAt;
}
