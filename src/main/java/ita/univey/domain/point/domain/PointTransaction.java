package ita.univey.domain.point.domain;

import ita.univey.domain.common.BaseEntity;
import ita.univey.domain.survey.domain.Survey;
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
    @JoinColumn(name = "users", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey", nullable = false)
    private Survey survey;

    @Column(name = "point_type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private PointType pointType;

    @Column(name = "point_amount", nullable = false, updatable = false)
    private Integer pointAmount;

    @Column(name = "remaining_amount", nullable = false, updatable = false)
    private Integer remainingPoints;


    @Builder
    public PointTransaction(User user, PointType pointType, Integer pointAmount, Survey survey, Integer remainingPoints) {
        this.user = user;
        this.pointType = pointType;
        this.pointAmount = pointAmount;
        this.survey = survey;
        this.remainingPoints = remainingPoints;
    }
}
