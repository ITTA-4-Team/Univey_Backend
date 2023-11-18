package ita.univey.domain.survey.domain;

import ita.univey.domain.category.domain.Category;
import ita.univey.domain.common.BaseEntity;
import ita.univey.domain.survey.domain.repository.Gender;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Survey")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Where(clause = "status = 'ACTIVE'")
public class Survey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "topic")
    private String topic;

    @Column(name = "description")
    private String description;

    @Column(name = "age")
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "deadline")
    private String deadline;

    @Column(name = "target_respondents")
    private Integer targetRespondents;

    @Column(name = "trend")
    private String trend;


    @Builder
    public Survey(String topic, String description, Integer age, Gender gender, String deadline, Integer targetRespondents, String trend) {
        this.topic = topic;
        this.description = description;
        this.age = age;
        this.gender = gender;
        this.deadline = deadline;
        this.targetRespondents = targetRespondents;
        this.trend = trend;
    }
}
