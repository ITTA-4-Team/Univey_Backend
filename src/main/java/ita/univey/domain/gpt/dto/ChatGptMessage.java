package ita.univey.domain.gpt.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ChatGptMessage implements Serializable {
    private String role;
    private String content;



    @Builder
    public ChatGptMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
