package ita.univey.domain.survey.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationReqDto {
    private List<ParticipationAnswerDto> answers;
}
