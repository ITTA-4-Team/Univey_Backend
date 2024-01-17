package ita.univey.domain.survey.domain.dto;

import lombok.*;

import java.time.LocalDate;

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
    private AgeDto age;
    private Integer currentRespondents;
    private Integer targetRespondents;
    private Integer point;
    private int questionCount;
    private String status;
    private boolean trend;
    private LocalDate dead_line;
}
