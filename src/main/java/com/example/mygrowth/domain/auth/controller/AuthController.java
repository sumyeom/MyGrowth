package com.example.mygrowth.domain.auth.controller;

import com.example.mygrowth.domain.auth.dto.LoginTokenRequestDto;
import com.example.mygrowth.domain.auth.dto.LoginTokenResponseDto;
import com.example.mygrowth.domain.auth.dto.SignupRequestDto;
import com.example.mygrowth.domain.auth.dto.SignupResponseDto;
import com.example.mygrowth.domain.auth.service.AuthService;
import com.example.mygrowth.domain.auth.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * 회원 가입 API
     * @param requestDto 회원 가입 dto
     * @return 가입한 유저 정보 ResponseEntity
     */
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(
            @Valid @RequestBody SignupRequestDto requestDto
    ){
        SignupResponseDto responseDto = authService.signup(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginTokenResponseDto> login(
            @Valid @RequestBody LoginTokenRequestDto requestDto,
            HttpServletResponse response
    ){
        LoginTokenResponseDto responseDto = authService.login(requestDto, response);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authorizationHeader,
            HttpServletResponse response
    ){
        String accessToken = authorizationHeader.replace("Bearer ", "");
        authService.logout(accessToken, response);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginTokenResponseDto> refresh(HttpServletRequest request){
        LoginTokenResponseDto newAccessToken = authService.refresh(request);
        return new ResponseEntity<>(newAccessToken, HttpStatus.OK);
    }

}
