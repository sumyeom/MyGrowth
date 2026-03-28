package com.example.mygrowth.global.filter;

import com.example.mygrowth.domain.auth.service.BlacklistService;
import com.example.mygrowth.global.provider.JwtProvider;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {
    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private BlacklistService blacklistService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("블랙리스트 토큰이면 인증되지 않는다.")
    void shouldNotAuthenticationWhenTokenISBlacklisted() throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/test");
        request.addHeader("Authorization", "Bearer blacklisted-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(blacklistService.isBlacklisted("blacklisted-token")).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtProvider,never()).validToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("정상 토큰일때는 인증된다.")
    void shouldAuthenticationWhenTokenIsValid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/test");
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        var userDetails = User.withUsername("test@test.com")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        when(blacklistService.isBlacklisted("valid-token")).thenReturn(false);
        when(jwtProvider.validToken("valid-token")).thenReturn(true);
        when(jwtProvider.getEmailFromToken("valid-token")).thenReturn("test@test.com");
        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(userDetails);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("test@test.com");
    }
}
