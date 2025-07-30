package com.example.mygrowth.domain.auth.service;

import com.example.mygrowth.domain.auth.dto.LoginTokenRequestDto;
import com.example.mygrowth.domain.auth.dto.LoginTokenResponseDto;
import com.example.mygrowth.domain.auth.dto.SignupRequestDto;
import com.example.mygrowth.domain.auth.dto.SignupResponseDto;
import com.example.mygrowth.domain.user.entity.User;
import com.example.mygrowth.domain.user.enums.UserStatus;
import com.example.mygrowth.domain.user.repository.UserRepository;
import com.example.mygrowth.global.constant.ErrorCode;
import com.example.mygrowth.global.exception.ApiException;
import com.example.mygrowth.global.provider.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final BlacklistService blacklistService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public SignupResponseDto signup(SignupRequestDto requestDto) {
        // 기존 사용자 체크
        Optional<User> findUser = userRepository.findByEmail(requestDto.getEmail());

        if(findUser.isPresent()) {
            if(findUser.get().getUserStatus() == UserStatus.WITHDRAW){
                throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
            }
            throw new ApiException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 비밀번호 암호화
        String rawPassword = requestDto.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 사용자 생성
        User newUser = new User(requestDto.getEmail(),requestDto.getName(), requestDto.getNickname(),encodedPassword );
        User savedUser = userRepository.save(newUser);

        return new SignupResponseDto(savedUser);
    }

    public LoginTokenResponseDto login(LoginTokenRequestDto requestDto, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword())
        );

        User findUser = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if(findUser.getUserStatus() == UserStatus.WITHDRAW){
            throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
        }

        if(!passwordEncoder.matches(requestDto.getPassword(),findUser.getPassword())){
            throw new ApiException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = tokenService.generateAccessTokens(findUser.getEmail(), findUser.getRole().getName());
        String refreshToken = tokenService.generateRefreshToken(findUser.getEmail());

        refreshTokenService.saveRefreshToken(findUser.getEmail(), refreshToken, response);
        return new LoginTokenResponseDto(accessToken);
    }

    public void logout(String accessToken, HttpServletResponse response) {
        String email = jwtProvider.getEmailFromToken(accessToken);

        // 블랙리스트에 Access Token 등록
        long expiration = jwtProvider.getRemainingTimeFromToken(accessToken);
        blacklistService.blacklistToken(accessToken,expiration);

        // Refresh Token 삭제
        refreshTokenService.removeRefreshToken(email, response);
    }

    public LoginTokenResponseDto refresh(HttpServletRequest request){
        String refreshToken = refreshTokenService.extractRefreshTokenFromCookie(request);

        if(refreshToken==null){
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }

        String email = jwtProvider.getEmailFromToken(refreshToken);
        String savedToken = refreshTokenService.getRefreshToken(email);

        if(!refreshToken.equals(savedToken)){
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }

        String newAccessToken = tokenService.reissueAccessToken(refreshToken);
        return new LoginTokenResponseDto(newAccessToken);
    }

}
