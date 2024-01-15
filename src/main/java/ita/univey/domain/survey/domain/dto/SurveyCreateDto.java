package ita.univey.domain.survey.domain.dto;

import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyCreateDto {
    @NotEmpty
    private String topic;

    @NotEmpty
    private String description;

    @NotEmpty
    private String category;

    private AgeDto age;

    @NotEmpty
    private String gender;

    @Column(name = "deadline")
    private String deadline;

    @Column(name = "target_respondents")
    private Integer targetRespondents;


}
