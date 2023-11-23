package ita.univey.global.error;

import ita.univey.global.BaseResponse;
import ita.univey.global.ErrorCode;
import ita.univey.global.SuccessCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(annotations = RestControllerAdvice.class)
public class ApiControllerAdvice {

    // Handle HttpRequestMethodNotSupportedException
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse<Object>> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        ErrorCode errorCode = new ErrorCode(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed");
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(BaseResponse.error(errorCode, e.getMessage()));
    }

    // Handle MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        ErrorCode errorCode = new ErrorCode(HttpStatus.BAD_REQUEST, "Validation Error");
        return ResponseEntity
                .badRequest()
                .body(BaseResponse.error(errorCode, errors.toString()));
    }

    // Generic Exception Handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<String>> handleGenericException(Exception e) {
        ErrorCode errorCode = new ErrorCode(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error(errorCode, e.getMessage()));
    }

    // Success response example
    public ResponseEntity<BaseResponse<String>> handleSuccessResponse() {
        SuccessCode successCode = new SuccessCode(HttpStatus.OK, "Success Message");
        return ResponseEntity
                .ok()
                .body(BaseResponse.success(successCode, "Your success data here"));
    }



}
