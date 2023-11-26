package ita.univey.domain.payment.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChargingHistoryDto { // 결제 내역을 반환하기 위한 응답 데이터

    private Long paymentHistoryId;

    @NonNull
    private Long amount;

    @NonNull
    private String orderName;

    private boolean isPaySuccessYN; //결제 성공 여부

    private LocalDateTime createdAt; //생성 시각

}
