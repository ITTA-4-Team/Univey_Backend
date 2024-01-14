package ita.univey.domain.payment.entity;

import ita.univey.domain.common.BaseEntity;
import ita.univey.domain.payment.dto.PaymentResDto;
import ita.univey.domain.user.domain.User;
import lombok.*;

import javax.persistence.*;

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
    private Integer amount;

    @Column(nullable = false, name = "pay_name")
    private String orderName;

    @Column(nullable = false, name = "order_id")
    private String orderId;

    private boolean paySuccessYN;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customer")
    private User user;

    @Column
    private String paymentKey;

    @Column
    private String failReason;

    @Column
    private boolean cancelYN;

    @Column
    private String cancelReason;

    public PaymentResDto toPaymentResDto() {  //DB에 저장하게 될 결제 정보들, 응답 결과로는 entity가 아닌 PaymentResDto 객체로 반환함
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