package ita.univey.domain.survey.domain.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
public class SurveyListDto {
    private Long id;
    private String topic;
    private String description;
    private String category;
    private Integer time;
    private Integer age;
    private Integer currentRespondents;
    private Integer targetRespondents;
    private Integer point;
    private String status;
    private boolean trend;
    private LocalDate dead_line;
}
