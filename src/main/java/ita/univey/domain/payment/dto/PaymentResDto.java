package ita.univey.domain.payment.dto;

import lombok.*;

//paymentDto로 받은 정보들 검증 후, 실제 토스페이먼츠에서 결제 요청을 하기 위한 값들을 포함해서 PaymentResDto로 반환
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResDto {

    private String payType;
    private Integer amount;
    private String orderName;
    private String orderId; //주문 Id
    private String userEmail; // 고객 이메일
    private String userName; // 고객 이름
    private String successUrl;
    private String failUrl;
    private String failReason; //실패 이유
    private boolean cancelYN; //취소 YN
    private String cancelReason; //취소 이유
    private String createdAt; //결제 이루어진 시간
}
