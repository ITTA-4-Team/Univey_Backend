package ita.univey.global;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    // api 만들고 수정하기

    CUSTOM_SUCCESS(OK, "~ 조회에 성공했습니다."),
    CUSTOM_CREATED_SUCCESS(CREATED, "~ 생성에 성공했습니다."),
    CUSTOM_QUESTION_SUCCESS(OK, "ChatGPT 요청에 성공했습니다."),
    SURVEY_RETRIEVED_SUCCESS(OK, "Survey 조회에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
