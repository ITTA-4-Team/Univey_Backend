package ita.univey.domain.survey.domain;

import ita.univey.domain.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "survey_image")
public class SurveyImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @Column(length = 500, nullable = false)
    private String link;

    @Builder
    public SurveyImage(Survey product, String link) {
        this.survey = survey;
        this.link = link;
    }

}