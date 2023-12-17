package ita.univey.domain.survey.domain;

import ita.univey.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Survey_Question")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Where(clause = "status = 'ACTIVE'")
public class SurveyQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;
    
    @Column(name = "question")
    private String question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @OneToMany(mappedBy = "question", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<SurveyQuestionAnswer> surveyQuestionAnswers;

    @Builder
    public SurveyQuestion(String question, Survey survey, List<SurveyQuestionAnswer> surveyQuestionAnswers) {
        this.question = question;
        this.survey = survey;
        this.surveyQuestionAnswers = surveyQuestionAnswers;
    }


}
