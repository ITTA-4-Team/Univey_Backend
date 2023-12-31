package ita.univey.domain.user.domain.dto;

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
    private String topic;

    @NotEmpty
    private String description;

    @NotEmpty
    private String category;

    @NotEmpty
    private String createdDay;

    private String deadline;

    @NotEmpty
    private Integer point;
}
