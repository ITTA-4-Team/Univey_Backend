package ita.univey.domain.survey.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ResultDto {
    private Long id;
    private String topic;
    private String description;
    private List<ResultQuestionDto> question;
}
