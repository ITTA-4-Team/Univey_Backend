package ita.univey.global;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // api 만들고 수정하기
    REQUEST_VALIDATION_EXCEPTION(BAD_REQUEST, "잘못된 요청입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "만료된 토큰입니다."),
    DUPLICATE_PARTICIPATION(CONFLICT, "중복 참여입니다."),

    PAYMENT_NOT_FOUND(BAD_REQUEST, "PAYMENT_NOT_FOUND"),
    PAYMENT_AMOUNT_EXP(BAD_REQUEST, "PAYMENT_AMOUNT_NOT_EQUAL"),
    ALREADY_APPROVED(CONFLICT, "ALREADY_APPROVED"),
    PAYMENT_NOT_ENOUGH_POINT(BAD_REQUEST, "PAYMENT_NOT_ENOUGH_POINT"),

    USER_NONE(BAD_REQUEST,"USER_NONE");

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
