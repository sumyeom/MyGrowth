package com.example.mygrowth.global.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /* 400 잘못된 입력값 */
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "잘못된 요청입니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", "잘못된 요청입니다."),
    IS_ALREADY_FOLLOWED(HttpStatus.BAD_REQUEST, "IS_ALREADY_FOLLOWED", "이미 팔로우 한 회원입니다."),
    IS_WITHDRAWN_USER(HttpStatus.BAD_REQUEST, "IS_WITHDRAW_USER", "비활성화된 회원입니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "DUPLICATE_USER", "이미 사용 중인 이메일입니다."),
    EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "EMAIL_NOT_FOUND", "해당 사용자가 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "비밀번호가 일치하지 않습니다."),
    IS_ALREADY_PENDING(HttpStatus.BAD_REQUEST, "IS_ALREADY_PENDING", "이미 대기중인 요청이 있습니다."),
    IS_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "IS_ALREADY_PROCESSED", "이미 처리된 요청입니다."),
    IS_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "IS_ALREADY_EXIST", "존재하는 정보가 있습니다."),
    FILE_UPLOAD_ERROR(HttpStatus.BAD_REQUEST,"FILE_UPLOAD_ERROR","파일 업로드 중 오류가 발생했습니다."),
    INVALID_PROVIDER_TYPE(HttpStatus.BAD_REQUEST,"INVALID_PROVIDER_TYPE", "지원하지 않는 서비스 제공자입니다."),
    INVALID_OAUTH2_ATTRIBUTE(HttpStatus.BAD_REQUEST, "INVALID_OAUTH2_ATTRIBUTE", "인증 정보가 유효하지 않습니다."),
    VERIFICATION_TIME_EXPIRED(HttpStatus.BAD_REQUEST, "VERIFICATION_TIME_EXPIRED", "인증 시간이 만료되었습니다."),
    VERIFICATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "VERIFICATION_NOT_FOUND", "인증 정보를 찾을 수 없습니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "INVALID_VERIFICATION_CODE", "인증 코드가 일치하지 않습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "EMAIL_NOT_VERIFIED", "이메일 인증이 필요합니다"),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST,"INVALID_FILE_EXTENSION", "허용되지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST,"FILE_SIZE_EXCEEDED","파일 크기가 초과되었습니다."),
    SOCIAL_PASSWORD_ALREADY_SET(HttpStatus.BAD_REQUEST, "SOCIAL_PASSWORD_ALREADY_SET", "이미 비밀번호가 설정되었습니다."),
    SOCIAL_PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "SOCIAL_PASSWORD_REQUIRED", "소셜 로그인 계정은 비밀번호 설정이 필요합니다."),


    /* 401 인증 오류 */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "로그인이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),

    /* 403 권한 없음 */
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "권한이 없습니다."),
    SOCIAL_PASSWORD_FORBIDDEN(HttpStatus.FORBIDDEN, "SOCIAL_PASSWORD_FORBIDDEN", "소셜 로그인 계정의 비밀번호를 설정할 수 없습니다."),

    /* 404 찾을 수 없음 */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "회원을 찾을 수 없습니다."),


    /* 500 서버 오류 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"INTERNAL_SERVER_ERROR","서버 오류 입니다."),
    EMAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_SEND_ERROR", "이메일 전송에 문제가 발생했습니다."),

    private final HttpStatus status;
    private final String code;
    private final String message;
}