package ita.univey.domain.survey.domain.dto;

//import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SurveyDto {
    private String topic;
    private String description;
    private List<QuestionDto> userQuestions;
}
