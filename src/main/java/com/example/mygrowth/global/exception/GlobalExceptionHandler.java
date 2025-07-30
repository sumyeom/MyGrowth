package com.example.mygrowth.global.exception;

import com.example.mygrowth.global.constant.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import javax.security.sasl.AuthenticationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleCustomException(ApiException e) {
        log.info("errorHandler start");
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode,errorCode.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {

        return ResponseEntity.status(ex.getStatusCode())
                .body(ex.getReason());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("handleIllegalArgument", e);
        ErrorCode errorCode;
        if("유효하지 않은 토큰입니다.".equals(e.getMessage())) {
            errorCode = ErrorCode.INVALID_TOKEN;
        } else {
            errorCode = ErrorCode.INVALID_PARAMETER;
        }
        return handleExceptionInternal(errorCode, errorCode.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleIRuntime(RuntimeException e) {
        log.warn("handleIRuntime", e);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return handleExceptionInternal(errorCode, errorCode.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn("handleMethodArgumentNotValid", e);

        // 유효성 검사 에러 리스트 변환
        List<ErrorResponse.ValidationError> errors = e.getBindingResult().getFieldErrors().stream()
                .map(ErrorResponse.ValidationError::of)
                .collect(Collectors.toList());

        // ErrorResponse
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(ErrorCode.INVALID_PARAMETER.getStatus().value())
                .code(ErrorCode.INVALID_INPUT.name())
                .message("Validation failed")
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<Object> handleInsufficientAuthenticationException(InsufficientAuthenticationException e) {
        log.warn("Authentication exception", e);
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return handleExceptionInternal(errorCode, errorCode.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException e) {
        log.warn("Authentication exception", e);
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return handleExceptionInternal(errorCode, errorCode.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("handleAccessDeniedException", e);
        ErrorCode errorCode = ErrorCode.FORBIDDEN;
        return handleExceptionInternal(errorCode, errorCode.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("handleHttpMessageNotReadableException", e);
        ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.warn("handleDataIntegrityViolationException", e);
        ErrorCode errorCode = ErrorCode.IS_ALREADY_EXIST;
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        log.error("Unhandled exception occurred", e);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return handleExceptionInternal(errorCode);
    }


    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(makeErrorResponse(errorCode));
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(makeErrorResponse(errorCode, message));
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .code(errorCode.name())
                .message(message)
                .build();
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        log.warn("File size limit exceeded", exc);
        ErrorCode errorCode = ErrorCode.FILE_SIZE_EXCEEDED;
        return handleExceptionInternal(errorCode);
    }

}
