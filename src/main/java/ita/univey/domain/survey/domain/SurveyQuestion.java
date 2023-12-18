package ita.univey.domain.survey.domain;

import ita.univey.domain.common.BaseEntity;
import ita.univey.domain.survey.domain.repository.QuestionType;
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

    @Column(name = "question_num")
    private int questionNum;

    @Column(name = "is_required")
    private boolean isRequried;

    @Column(name = "question_type")
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @Column(name = "question")
    private String question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @OneToMany(mappedBy = "question", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @OrderColumn(name = "choice_order")
    private List<SurveyQuestionAnswer> surveyQuestionAnswers;

    @Builder
    public SurveyQuestion(int questionNum, boolean isRequried, QuestionType questionType, String question,
                          Survey survey, List<SurveyQuestionAnswer> surveyQuestionAnswers) {
        this.questionNum = questionNum;
        this.isRequried = isRequried;
        this.questionType = questionType;
        this.question = question;
        this.survey = survey;
        this.surveyQuestionAnswers = surveyQuestionAnswers;
    }


}
