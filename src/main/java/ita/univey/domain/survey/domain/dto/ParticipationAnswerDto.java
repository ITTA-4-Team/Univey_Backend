package ita.univey.domain.survey.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationAnswerDto {
    @NotNull
    private Long question_id;

    private Long answer_id;
    private String content;
}
