package com.example.mygrowth.domain.auth.controller;

import com.example.mygrowth.domain.auth.dto.LoginTokenRequestDto;
import com.example.mygrowth.domain.auth.dto.LoginTokenResponseDto;
import com.example.mygrowth.domain.auth.dto.SignupRequestDto;
import com.example.mygrowth.domain.auth.dto.SignupResponseDto;
import com.example.mygrowth.domain.auth.service.AuthService;
import com.example.mygrowth.domain.auth.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="Auth", description = "유저 관련 CRUD")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * 회원 가입 API
     * @param requestDto 회원 가입 dto
     * @return 가입한 유저 정보 dto
     */
    @PostMapping("/signup")
    @Operation(summary = "회원 가입", description = "입력된 유저 정보로 회원을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "회원 가입 성공")
    public ResponseEntity<SignupResponseDto> signup(
            @Valid @RequestBody @Schema(implementation = SignupRequestDto.class) SignupRequestDto requestDto
    ){
        SignupResponseDto responseDto = authService.signup(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * 로그인 API
     * @param requestDto 이메일/비밀번호 dto
     * @param response response
     * @return accessToken
     */
    @PostMapping("/login")
    @Operation(summary="로그인", description = "이메일/비밀번호를 입력받아 로그인합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    public ResponseEntity<LoginTokenResponseDto> login(
            @Valid @RequestBody @Schema(implementation = LoginTokenRequestDto.class) LoginTokenRequestDto requestDto,
            HttpServletResponse response
    ){
        LoginTokenResponseDto responseDto = authService.login(requestDto, response);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 로그아웃 API
     * @param authorizationHeader authorizationHeader
     * @param response response
     * @return void
     */
    @PostMapping("/logout")
    @Operation(summary="로그아웃", description = "로그아웃합니다.")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authorizationHeader,
            HttpServletResponse response
    ){
        String accessToken = authorizationHeader.replace("Bearer ", "");
        authService.logout(accessToken, response);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * AccessToken 재발급 요청
     * @param request request
     * @return accessToken
     */
    @PostMapping("/refresh")
    @Operation(summary = "Access Token 재발급 요청", description = "Refresh Token으로 Access Token 재발급 요청합니다.")
    @ApiResponse(responseCode = "200", description = "accessToken 재발급 요청 성공")
    public ResponseEntity<LoginTokenResponseDto> refresh(HttpServletRequest request){
        LoginTokenResponseDto newAccessToken = authService.refresh(request);
        return new ResponseEntity<>(newAccessToken, HttpStatus.OK);
    }

}
