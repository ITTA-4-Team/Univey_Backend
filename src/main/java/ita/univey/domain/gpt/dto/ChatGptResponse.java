package ita.univey.domain.gpt.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatGptResponse {
    private String question;
    private List<String> answer;


}
