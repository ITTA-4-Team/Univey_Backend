package ita.univey.domain.payment.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChargingHistoryDto {

    private Long paymentHistoryId;

    @NonNull
    private Long amount;

    @NonNull
    private String orderName;

    private boolean isPaySuccessYN; //결제 성공 여부

    private LocalDateTime createdAt; //생성 시각

}
