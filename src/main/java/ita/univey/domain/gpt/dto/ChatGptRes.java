package ita.univey.domain.gpt.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class ChatGptRes {
    private Long question_num;
    private String question;
    private Map<String, String> answers;
}
