package ita.univey.domain.payment.dto;

import lombok.Data;

@Data
public class PaymentSuccessDto {
    String mid; // 가맹점 id -> tosspayments
    String version; //payment 객체 응답 버전
    String paymentKey;
    String orderId;
    String orderName;
    String currency; // "KRW"
    String method; // 결제 수단
    String totalAmount;
    String balanceAmount;
    String suppliedAmount;
    String vat; // 부가가치세
    String status; // 결제 처리 상태
    String requestedAt;
    String approvedAt;
    String useEscrow; // false
    String cultureExpense; // false
    PaymentSuccessCardDto card; // 결제 카드 정보 (별도의 dto 정의)
    String type; // 결제 타입 정보 ( NORMAL, BILLING, CONNECTPAY)
}