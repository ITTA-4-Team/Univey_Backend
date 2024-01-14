package ita.univey.domain.gpt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatGptResponse {
    @NotEmpty
    @JsonProperty("question_num")
    private Integer questionNum;

    @NotEmpty
    @JsonProperty("question_type")
    private String questionType;

    @NotEmpty
    private boolean isRequired;

    private String question;
    private List<String> answer;


}
