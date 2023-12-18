package ita.univey.domain.survey.domain.dto;

import ita.univey.domain.gpt.dto.ChatGptRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyDetailsResponse {

    @NotEmpty
    private Long id;

    @NotEmpty
    private String topic;

    @NotEmpty
    private String description;

    private List<ChatGptRes> recommendedQuestions;
}
