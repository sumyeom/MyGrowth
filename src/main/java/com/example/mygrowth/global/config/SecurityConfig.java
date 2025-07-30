package com.example.mygrowth.global.config;

import com.example.mygrowth.global.config.auth.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * spring security 관련 구현체 클래스 설정
 */

@Configuration
@RequiredArgsConstructor
@Slf4j(topic = "Security::SecurityConfig")
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    /**
     * PasswordEncoder(암호 처리기)
     *
     * return (@link BCryptPasswordEncoder)
     */
    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager(인증 관리자)
     *
     * @param config(@link AuthenticationConfiguration)
     * @return 설정이 추가된 AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
        throws Exception{
        log.info("AuthenticationManager에 위임");
        return config.getAuthenticationManager();
    }

    /**
     * AuthenticationProvider(인증 공급자)
     *
     * @return {@link AuthenticationProvider}
     */
    @Bean
    AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        log.info("AuthenticationProvider 설정. 구현체 : {}", authProvider.getClass().getSimpleName());

        log.info("userDetailsService에 사용자 관리 위임. 구현체 : {}",
                this.userDetailsService.getClass().getSimpleName());
        authProvider.setUserDetailsService(this.userDetailsService);

        log.info("PasswordEncoder에 암호 검증 위임. 구현체 : {}",
                this.passwordEncoder().getClass().getSimpleName());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}

