package ita.univey.domain.survey.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyCreateDto {
    @NotEmpty
    private String topic;

    @NotEmpty
    private String description;

    @NotEmpty
    private String category;

    private int age;

    @NotEmpty
    private String gender;

    @Column(name = "deadline")
    private String deadline;

    @Column(name = "target_respondents")
    private Integer targetRespondents;


}
