package ita.univey.domain.survey.domain.dto;

import ita.univey.domain.survey.domain.repository.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDto {
    private Long question_id;
    private int question_num;
    private boolean isRequired;
    private QuestionType question_type;
    private String question;
    private List<SurveyQuestionAnswerDto> answers;
}
