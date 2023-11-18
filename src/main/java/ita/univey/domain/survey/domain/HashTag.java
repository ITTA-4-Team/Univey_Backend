package ita.univey.domain.survey.domain;

import ita.univey.domain.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "hashtag")
public class HashTag extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @Column(length = 20, nullable = false)
    private String content;

    @Builder
    public HashTag(Survey survey, String content) {
        this.survey = survey;
        this.content = content;
    }
}