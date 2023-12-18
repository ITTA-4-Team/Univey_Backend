package ita.univey.domain.category.domain;

import ita.univey.domain.survey.domain.Survey;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "Category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Where(clause = "status = 'ACTIVE'")
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "category")
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

}
