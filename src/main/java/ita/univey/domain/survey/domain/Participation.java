package ita.univey.domain.survey.domain;

import ita.univey.domain.common.BaseEntity;
import ita.univey.domain.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "Participation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Where(clause = "status = 'ACTIVE'")
public class Participation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey")
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_question")
    private SurveyQuestion surveyQuestion;

    @Column(name = "content")
    private String content;

    @Builder
    public Participation(User user, Survey survey, SurveyQuestion surveyQuestion, String content) {
        this.user = user;
        this.survey = survey;
        this.surveyQuestion = surveyQuestion;
        this.content = content;
    }
}
