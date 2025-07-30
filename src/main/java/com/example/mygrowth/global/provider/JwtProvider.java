package com.example.mygrowth.global.provider;

import com.example.mygrowth.domain.user.entity.User;
import com.example.mygrowth.domain.user.enums.Role;
import com.example.mygrowth.domain.user.repository.UserRepository;
import com.example.mygrowth.global.constant.ErrorCode;
import com.example.mygrowth.global.exception.ApiException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import static io.jsonwebtoken.Jwts.header;
import static io.jsonwebtoken.Jwts.parserBuilder;

/**
 * JWT 제공자
 * 토큰의 생성, 추출, 만료 확인 등의 기능
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    // Access Token
    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    // Refresh Token
    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    /**
     * Access Token 생성
     * @param email
     * @param role
     * @return
     */
    public String generateAccessToken(String email, String role){
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + accessTokenExpiration);
        Key signingKey = generateSigningKey();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Refresh Token 생성
     * @param email
     * @return
     */
    public String generateRefreshToken(String email){
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshTokenExpiration);
        Key signingKey = generateSigningKey();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(signingKey)
                .compact();
    }


    private Key generateSigningKey() {
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }


    /**
     * JWT의 Claims 부분추출
     * @param token
     * @return
     */
    private Claims getTokenClaims(String token) {
        if(!StringUtils.hasText(token)){
            throw new MalformedJwtException("Invalid JWT token");
        }

        return parserBuilder()
                .setSigningKey(generateSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 이메일 추출
     * @param token
     * @return
     */
    public String getEmailFromToken(String token){
        return getTokenClaims(token).getSubject();
    }

    /**
     * 만료시간 
     * @param token
     * @return
     */
    public long getExpirationFromToken(String token){
        return getTokenClaims(token).getExpiration().getTime();
    }

    /**
     * 남은 시간
     * @param token
     * @return
     */
    public long getRemainingTimeFromToken(String token){
        Date expiration = getTokenClaims(token).getExpiration();
        long remaining = expiration.getTime() - System.currentTimeMillis();
        log.info("Token expiration left: {}ms", remaining);
        return remaining;
    }

    /**
     * Role
     * @param token
     * @return
     */
    public String getRoleFromToken(String token){
        return getTokenClaims(token).get("role", String.class);
    }

    /**
     * 토큰 유효한지 확인
     * @param token
     * @return
     */
    public boolean validToken(String token) throws JwtException {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(generateSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch(MalformedJwtException e){
            log.error("Invalid JWT token : {} ", e.getMessage());
        }catch(ExpiredJwtException e){
            log.error("Expired JWT token : {} ", e.getMessage());
        }catch(UnsupportedJwtException e){
            log.error("Unsupported JWT token : {} ", e.getMessage());
        }
        return false;
    }


}


