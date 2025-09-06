package com.example.mygrowth.global.config;

import com.example.mygrowth.global.filter.JwtAuthenticationFilter;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;

/**
 * 웹 설정
 *
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final AuthenticationEntryPoint authEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    /**
     * security 필터
     *
     * @param http {@link HttpSecurity}
     * @return {@link SecurityFilterChain} 필터 체인
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/signup","/api/auth/login","/api/auth/refresh").permitAll()
                                //static 리스트 경로
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                // 일부 dispatch 타입
                                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE,
                                        DispatcherType.ERROR).permitAll()
                                // path 별로 접근이 가능한 권한 설정
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/users/**").hasRole("USER")
                                // 나머지는 인증이 필요
                                .anyRequest().authenticated()
                )
                // Spring Security 예외에 대한 처리를 핸들러에 위임
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterAfter(jwtAuthFilter, ExceptionTranslationFilter.class);

        return http.build();
    }

    /**
     * 사용자 권한의 계층을 설정
     *
     * @return {@link RoleHierarchy}
     */
    @Bean
    public RoleHierarchy roleHierarchy(){
        return RoleHierarchyImpl.fromHierarchy(
                """
                        ROLE_ADMIN > ROLE_USER
                        """
        );
    }


}


