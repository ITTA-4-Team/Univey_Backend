package ita.univey.domain.gpt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
//Front단에서 요청하는 DTO
public class QuestionReq implements Serializable {
    @JsonProperty("question") // 이 애너테이션이 필요할 수 있음
    private String question;
}
