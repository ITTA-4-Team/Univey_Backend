package ita.univey.domain.survey.domain.dto;

import lombok.*;

import javax.persistence.Column;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgeDto {

    @Column(name = "min_Age")
    Integer minAge;

    @Column(name = "max_Age")
    Integer maxAge;
}
