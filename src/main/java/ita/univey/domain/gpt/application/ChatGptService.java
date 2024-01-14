package ita.univey.domain.gpt.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import ita.univey.domain.gpt.config.ChatGptConfig;
import ita.univey.domain.gpt.dto.ChatGptMessage;
import ita.univey.domain.gpt.dto.ChatGptReq;
import ita.univey.domain.gpt.dto.ChatGptRes;
import ita.univey.domain.gpt.dto.QuestionReq;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatGptService {
    private RestTemplate restTemplate = new RestTemplate();

    @Value("${chatgpt.api-key}")
    private String apiKey;

    //라이브러리 제공 서비스
    private final ChatgptService chatgptService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    public HttpEntity<ChatGptReq> buildHttpEntity(ChatGptReq chatGptRequest) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.parseMediaType(ChatGptConfig.MEDIA_TYPE));
        httpHeaders.add(ChatGptConfig.AUTHORIZATION, ChatGptConfig.BEARER + apiKey);
        return new HttpEntity<>(chatGptRequest, httpHeaders);
    }

    public ChatGptRes getResponse(HttpEntity<ChatGptReq> chatGptRequestHttpEntity) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(60000);
        //답변이 길어질 경우 TimeOut Error가 발생하니 1분정도 설정해줍니다.
        requestFactory.setReadTimeout(60 * 1000);   //  1min = 60 sec * 1,000ms
        restTemplate.setRequestFactory(requestFactory);

        ResponseEntity<ChatGptRes> responseEntity = restTemplate.postForEntity(
                ChatGptConfig.CHAT_URL,
                chatGptRequestHttpEntity,
                ChatGptRes.class);

        return responseEntity.getBody();
    }


    public ChatGptRes ask(QuestionReq questionReq) throws JsonProcessingException {
        // 요청된 키워드에 따라 설문조사 질문 생성
        List<ChatGptMessage> messages = new ArrayList<>();


        messages.add(ChatGptMessage.builder()
                .role(ChatGptConfig.ROLE)
//                .content(
//                        "Please generate 3 survey questions with 5 multiple choice answers each, related to the topic: '"
//                                + questionReq.getQuestion()
//                                + "한국말로만 보여줘 json 형태로 질문은 question에 담아서 answer는 answer배열에 담아서 출력해줘. answer에는 문장 부호를 포함하지않아 , 같은 ")
                .content("[[1, 개발 프로젝트에서 가장 선호하는 언어는 무엇인가요?, 자바, 파이썬, 자바스크립트, C++, 기타],[2, 개발 프로젝트에서 가장 자주 사용하는 개발 도구는 무엇인가요?, 이클립스, 비주얼 스튜디오 코드, 파이참, 인텔리J IDEA, 기타],[3, 개발 프로젝트에서 가장 중요하게 생각하는 요소는 무엇인가요?, 코드의 가독성, 성능 최적화, 보안, 사용자 경험, 기타]]\n" +
                        "이 형태를 예시로" + questionReq.getQuestion() + "에 대한 설문조사에 필요한 객관식 3개. 3개는 각각 5개의 보기를 갖게 알려줘.위에 알려준 형태를 예시로 저 형태로 보내줘")

                .build());

        ChatGptRes chatGptRes = this.getResponse(
                this.buildHttpEntity(
                        new ChatGptReq(
                                ChatGptConfig.CHAT_MODEL,
                                ChatGptConfig.MAX_TOKEN,
                                ChatGptConfig.TEMPERATURE,
                                ChatGptConfig.STREAM,
                                messages
                        )
                )
        );


        String content = chatGptRes.getChoices().get(0).getMessage().getContent();

        String jsonFormattedString = convertToJSONFormat(content);


        chatGptRes.getChoices().get(0).getMessage().setContent(jsonFormattedString);


        return chatGptRes;


    }

    private String convertToJSONFormat(String content) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");

        String[] lines = content.split("\n");
        ArrayList<String> questionAndAnswers = new ArrayList<>();
        int questionIndex = 1; // 질문 번호 초기화

        for (String line : lines) {
            line = line.trim(); // 공백 제거
            line = line.replaceAll("^\\d+\\.", ""); // '1.', '2.', '3.', 등 제거
            line = line.replaceAll("\\b[a-e]\\)\\s*", "").trim(); // 'a)', 'b)', 'c)', 'd)', 'e)' 제거
            line = line.replaceAll("\\b[a-e]\\.", "").trim(); // 'a.', 'b.', 'c.', 'd.', 'e.' 제거
            line = line.replace("\"", ""); // 큰따옴표 제거

            if (!line.isEmpty()) {
                questionAndAnswers.add(line);
            } else {
                if (!questionAndAnswers.isEmpty()) {
                    questionAndAnswers.add(0, String.valueOf(questionIndex)); // 질문 번호를 배열의 첫 번째 요소로 추가
                    jsonBuilder.append("[").append(String.join(", ", questionAndAnswers)).append("],");
                    questionAndAnswers.clear();
                    questionIndex++; // 다음 질문 번호로 업데이트
                }
            }
        }

        // 마지막 질문과 선택지 추가
        if (!questionAndAnswers.isEmpty()) {
            questionAndAnswers.add(0, String.valueOf(questionIndex)); // 질문 번호를 배열의 첫 번째 요소로 추가
            jsonBuilder.append("[").append(String.join(", ", questionAndAnswers)).append("]");
        } else {
            if (jsonBuilder.length() > 1) {
                jsonBuilder.setLength(jsonBuilder.length() - 1); // 마지막 콤마 제거
            }
        }

        jsonBuilder.append("]");

        return jsonBuilder.toString();
    }


}
