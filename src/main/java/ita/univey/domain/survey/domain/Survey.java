package ita.univey.domain.survey.domain;

import ita.univey.domain.category.domain.Category;
import ita.univey.domain.common.BaseEntity;
import ita.univey.domain.survey.domain.repository.Gender;
import ita.univey.domain.survey.domain.repository.SurveyStatus;
import ita.univey.domain.user.domain.User;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Survey")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Where(clause = "status = 'ACTIVE'")
public class Survey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    //설문 생성한 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users")
    private User user;

    @Column(name = "topic")
    private String topic;

    @Column(name = "description")
    private String description;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "target_respondents")
    private Integer targetRespondents;

    @Column(name = "current_respondents")
    private Integer currentRespondents;

    @Column(name = "survey_state")
    @Enumerated(EnumType.STRING)
    private SurveyStatus surveyState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cateogry")
    private Category category;

    @OneToMany(mappedBy = "survey", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
//    @OrderColumn(name = "question_order") front에서 questionNum으로 순서 구분해서 생략.
    private List<SurveyQuestion> surveyQuestions;

    @Column(name = "point")
    private Integer point;

    @Column(name = "trend")
    private boolean trend;

    @Column(name = "time")
    private Integer time;

    @Builder
    public Survey(User user, String topic, String description, Integer minAge, Integer maxAge, Gender gender,
                  LocalDate deadline, Integer targetRespondents, Integer currentRespondents,
                  SurveyStatus surveyState, Category category, List<SurveyQuestion> surveyQuestions,
                  boolean trend, Integer time) {
        this.user = user;
        this.topic = topic;
        this.description = description;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.gender = gender;
        this.deadline = deadline;
        this.targetRespondents = targetRespondents;
        this.currentRespondents = (currentRespondents != null) ? currentRespondents : 0; //null일 경우 0으로 저장.;
        this.surveyState = surveyState;
        this.category = category;
        this.surveyQuestions = surveyQuestions;
        this.trend = trend;
        this.time = time;
    }

    public void updateSurveyPoint(int point) {
        this.point = point;
    }

    public void endSurvey() {
        this.surveyState = SurveyStatus.COMPLETED;
    }
}
