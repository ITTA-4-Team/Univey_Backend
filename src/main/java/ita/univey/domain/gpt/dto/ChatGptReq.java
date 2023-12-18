package ita.univey.domain.gpt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
//chatGPT에 요청할 DTO Format
public class ChatGptReq implements Serializable {
    private String model;
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    private Double temperature;
    private Boolean stream;
    private List<ChatGptMessage> messages;


    @Builder
    public ChatGptReq(String model, Integer maxTokens, Double temperature, Boolean stream, List<ChatGptMessage> messages) {
        this.model = model;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
        this.stream = stream;
        this.messages = messages;
    }
}
