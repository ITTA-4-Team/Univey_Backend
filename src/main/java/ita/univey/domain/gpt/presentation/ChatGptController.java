//package ita.univey.domain.gpt.presentation;
//
//import io.github.flashvayne.chatgpt.service.ChatgptService;
//import ita.univey.domain.gpt.application.ChatGptService;
//import ita.univey.domain.gpt.dto.ChatGptRes;
//import ita.univey.domain.gpt.dto.QuestionReq;
//import ita.univey.global.BaseResponse;
//import ita.univey.global.ErrorCode;
//import ita.univey.global.SuccessCode;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.Locale;
//
//@RestController
//@RequestMapping("/chat-gpt")
//@RequiredArgsConstructor
//public class ChatGptController {
//
//    private final ChatGptService chatGptService;
//
//    @PostMapping("/question")
//    public ResponseEntity sendQuestion(
//            Locale locale,
//            HttpServletRequest request,
//            HttpServletResponse response,
//            @RequestBody QuestionReq questionReq) {
//        try {
//            ChatGptRes chatGptRes = chatGptService.ask(questionReq);
//            return ResponseEntity.ok(BaseResponse.success(SuccessCode.CUSTOM_QUESTION_SUCCESS, chatGptRes.getChoices().get(0).getMessage().getContent()));
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
//        }
//    }
//
//}
