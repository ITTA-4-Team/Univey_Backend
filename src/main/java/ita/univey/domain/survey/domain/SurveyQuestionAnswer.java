package ita.univey.domain.survey.domain;

import ita.univey.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "Survey_Question_Answer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Where(clause = "status = 'ACTIVE'")
public class SurveyQuestionAnswer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    private String answer;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private SurveyQuestion question;

    @Builder
    public SurveyQuestionAnswer(String answer, SurveyQuestion question) {
        this.answer = answer;
        this.question = question;
    }
}
