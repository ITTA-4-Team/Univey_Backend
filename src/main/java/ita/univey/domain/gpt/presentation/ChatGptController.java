package ita.univey.domain.gpt.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import ita.univey.domain.gpt.application.ChatGptService;
import ita.univey.domain.gpt.dto.ChatGptRes;
import ita.univey.domain.gpt.dto.ChatGptResponse;
import ita.univey.domain.gpt.dto.QuestionReq;
import ita.univey.global.BaseResponse;
import ita.univey.global.ErrorCode;
import ita.univey.global.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/chat-gpt")
@RequiredArgsConstructor
public class ChatGptController {

    private final ChatGptService chatGptService;

    @PostMapping("/question")
    public BaseResponse<List<ChatGptResponse>> sendQuestion(
            Locale locale,
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody QuestionReq questionReq) {
        try {
            ChatGptRes chatGptRes = chatGptService.ask(questionReq);
            String content = chatGptRes.getChoices().get(0).getMessage().getContent();
//            String content = (tmp.replaceAll("[\\[\\]{}\"]", ""));
            //log.info("gpt=>{}", chatGptRes.getChoices().get(0).getMessage().getContent());
            log.info("gpt2 =>{}", content);

            List<ChatGptResponse> responses = parseInput(content);

            return BaseResponse.success(SuccessCode.CUSTOM_QUESTION_SUCCESS, responses);

        } catch (Exception e) {
            return BaseResponse.error(ErrorCode.REQUEST_VALIDATION_EXCEPTION, e.getMessage());
        }
    }

    public static List<ChatGptResponse> parseInput(String input) {
        List<ChatGptResponse> responses = new ArrayList<>();

        String[] sections = input.split("],");
        for (String section : sections) {
            String[] parts = section.replace("[[", "").replace("]]", "").split(",");
            String question = parts[1].trim();
            List<String> answers = Arrays.asList(Arrays.copyOfRange(parts, 2, parts.length));
            responses.add(new ChatGptResponse(question, answers));
        }

        return responses;
    }
}
