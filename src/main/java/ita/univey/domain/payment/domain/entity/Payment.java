package ita.univey.domain.payment.domain.entity;

import javax.persistence.*;

import ita.univey.domain.common.BaseEntity;
import ita.univey.domain.payment.domain.dto.PaymentResDto;
import ita.univey.domain.user.domain.User;
import lombok.*;
import org.springframework.data.domain.Auditable;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Table(indexes = {
        @Index(name = "idx_payment_member", columnList = "customer"),
        @Index(name = "idx_payment_paymentKey", columnList = "paymentKey"),
})
public class Payment extends BaseEntity { //결제 요청 객체, 결제에 필요한 정보를 담아 DB에 저장

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, name = "pay_type")
    @Enumerated(EnumType.STRING)
    private PayType payType;

    @Column(nullable = false, name = "pay_amount")
    private Long amount;

    @Column(nullable = false, name = "pay_name")
    private String orderName;

    @Column(nullable = false, name = "order_id")
    private String orderId;

    private boolean paySuccessYN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String PaymentKey;

    @Column
    private String failReason;

    @Column
    private boolean cancelYN;

    public PaymentResDto toPaymentResDto() {
        return PaymentResDto.builder()
                .payType(payType.getDescription())
                .amount(amount)
                .orderName(orderName)
                .orderId(orderId)
                .userEmail(user.getEmail())
                .userName(user.getName())
                .createdAt(String.valueOf(getCreatedAt()))
                .cancelYN(cancelYN)
                .failReason(failReason)
                .build();
    }

}