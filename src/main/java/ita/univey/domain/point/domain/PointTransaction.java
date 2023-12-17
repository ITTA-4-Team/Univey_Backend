package ita.univey.domain.point.domain;

import ita.univey.domain.common.BaseEntity;
import ita.univey.domain.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "point_transaction")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PointTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @Column(name = "point_type", nullable = false, updatable = false)
    private PointType pointType;

    @Column(name = "point_amount", nullable = false, updatable = false)
    private Integer pointAmount;

    @Builder
    public PointTransaction(User user, PointType pointType, Integer pointAmount) {
        this.user = user;
        this.pointType = pointType;
        this.pointAmount = pointAmount;
    }
}
