package ita.univey.domain.category.domain;

import ita.univey.domain.common.BaseEntity;
import ita.univey.domain.survey.domain.Survey;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Where(clause = "status = 'ACTIVE'")
@AllArgsConstructor

public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "category")
    private String category;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY) // 일대다 양방향 관계로 수정
    private List<Survey> surveyList;

}
