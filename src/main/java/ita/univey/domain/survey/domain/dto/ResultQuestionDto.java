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
public class ResultQuestionDto {
    private int question_num;
    private QuestionType question_type;
    private String question;
    private List<String> answer;
    private List<Integer> votes;
}
