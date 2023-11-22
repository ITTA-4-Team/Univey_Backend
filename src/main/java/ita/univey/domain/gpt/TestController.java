package ita.univey.domain.gpt;

import io.github.flashvayne.chatgpt.service.ChatgptService;
import ita.univey.domain.gpt.application.ChatService;
import ita.univey.domain.gpt.dto.ChatGptRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/survey-gpt")
public class TestController {

    private final ChatService chatService;
    private final ChatgptService chatgptService;

    // 설문 조사 제목, 항목 추천 만들기 메서드
    @PostMapping("")
    public String test(@RequestBody String question) {
        // 조합된 질문 생성
        String combinedQuestion = "설문조사를 하려고하는데,/n" + question + "에 관한 흥미로운 설문조사 주제와 답변항목(5개) 임의로 만들어줘./n" + "json 형태로 만들어줘";
        String getResponse = chatService.getChatResponse(combinedQuestion);

        // GPT 응답을 파싱하여 dto로 변환
//        ChatGptRes chatGptRes = parseGptResponseToSurveyQuestion(String gptResponse);

        return getResponse;
    }
}
