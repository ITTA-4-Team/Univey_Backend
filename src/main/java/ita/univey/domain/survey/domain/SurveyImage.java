package ita.univey.domain.survey.domain;

import ita.univey.domain.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "Survey_Image")
public class SurveyImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @Column(length = 500, nullable = false)
    private String link;

    @Builder
    public SurveyImage(Survey survey, String link) {
        this.survey = survey;
        this.link = link;
    }
}
