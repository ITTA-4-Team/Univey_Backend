package ita.univey.domain.survey.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class SurveyQuestionsCreateDto {

    @NotEmpty
    private List<UserQuestions> userQuestions;

    @Getter
    public static class UserQuestions {
        @NotEmpty
        @JsonProperty("question_num")
        private Integer questionNum;

        @NotEmpty
        private String question;

        @NotEmpty
        @JsonProperty("question_type")
        private String questionType;

        @NotEmpty
        private boolean isRequired;

        @NotEmpty
        private List<String> answer;

    }
}