package ita.univey.domain.payHistory.entity;

import ita.univey.domain.common.BaseEntity;
import ita.univey.domain.user.domain.User;
import lombok.*;
import org.springframework.data.domain.Auditable;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PayHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payHistoryId;

    @Column(nullable = false)
    private Long paidPrice;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /*@Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Type type;*/

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Gain gain;

//    private Long referenceId; // 외래키

    public enum Gain {
        GAIN("획득"),
        PAY("지불"),
        CHARGE("충전");

        @Getter
        private String status;

        Gain(String status) {
            this.status = status;
        }
    }
}