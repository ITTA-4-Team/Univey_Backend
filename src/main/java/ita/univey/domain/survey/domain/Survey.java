package ita.univey.domain.survey.domain;

import ita.univey.domain.category.domain.Category;
import ita.univey.domain.common.BaseEntity;
import ita.univey.domain.survey.domain.repository.Gender;
import ita.univey.domain.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
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
    //설문 생성한 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private User user;

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
    private LocalDate deadline;

    @Column(name = "target_respondents")
    private Integer targetRespondents;

    @Column(name = "trend")
    private String trend;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cateogry")
    private Category category;

    @OneToMany(mappedBy = "survey", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
//    @OrderColumn(name = "question_order") front에서 questionNum으로 순서 구분해서 생략.
    private List<SurveyQuestion> surveyQuestions;

    @Builder
    public Survey(User user, String topic, String description, Integer age, Gender gender,
                  LocalDate deadline, Integer targetRespondents, String trend, Category category, List<SurveyQuestion> surveyQuestions) {
        this.user = user;
        this.topic = topic;
        this.description = description;
        this.age = age;
        this.gender = gender;
        this.deadline = deadline;
        this.targetRespondents = targetRespondents;
        this.trend = trend;
        this.category = category;
        this.surveyQuestions = surveyQuestions;
    }
}
