package ita.univey.global;

import lombok.Getter;

public class CustomLogicException extends RuntimeException{
    @Getter
    private ErrorCode errorCode;
    public CustomLogicException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}