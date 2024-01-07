package ita.univey.domain.user.domain.dto;

import ita.univey.domain.survey.domain.repository.SurveyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSurveyResponse {

    @NotEmpty
    private Long surveyId;

    @NotEmpty
    private SurveyStatus status;

    @NotEmpty
    private String topic;

    @NotEmpty
    private String description;

    private Integer age;

    @NotEmpty
    private String category;

    private Integer currentRespondents;

    private Integer targetRespondents;

    @NotEmpty
    private String createdDay;

    private String deadline;

    @NotEmpty
    private Integer point;
}
