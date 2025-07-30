package com.example.mygrowth.global.config.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * <p>인증 실패에 대한 응답을 구성.</p>
 *
 * @author : Jeon Su Yeon
 * @version 1.0
 * @since 1.0
 */
@Component
public class DelegatedAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Spring Security 예외를 처리하기 위한 resolver
     */
    private final HandlerExceptionResolver resolver;

    /**
     * 생성자
     */
    public DelegatedAuthenticationEntryPoint(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) {
        resolver.resolveException(request, response, null, authException);
    }
}
