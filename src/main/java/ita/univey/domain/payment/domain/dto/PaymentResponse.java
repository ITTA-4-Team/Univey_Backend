package ita.univey.domain.payment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "결제 승인 성공시 응답 DTO")
@Data
@ToString
public class PaymentResponse {
    @Schema(description = "상점 아이디")
    String mid;
    @Schema(description = "결제 버전")
    String version;
    @Schema(description = "결제 키")
    String paymentKey;
    @Schema(description = "주문 아이디")
    String orderId;
    @Schema(description = "주문명")
    String orderName;
    @Schema(description = "결제 통화")
    String currency;
    @Schema(description = "결제 수단")
    String method;
    @Schema(description = "총 결제 금액")
    String totalAmount;
    @Schema(description = "취소할 수 있는 금액")
    String balanceAmount;
    @Schema(description = "공급가액")
    String suppliedAmount;
    @Schema(description = "부가세")
    String vat;
    @Schema(description = "결제 상태")
    String status;
    @Schema(description = "결제가 일어난 날짜와 시간 정보")
    String requestedAt;
    @Schema(description = "결제 승인이 일어난 날짜와 시간 정보")
    String approvedAt;
    @Schema(description = "에스크로 사용정보")
    String useEscrow;
    @Schema(description = "문화비 지출 여부")
    String cultureExpense;
    @Schema(description = "카드 정보")
    PaymentCardDto card;
    @Schema(description = "결제 타입 정보")
    String type;
}