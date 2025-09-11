package com.example.mygrowth.domain.user.controller;


import com.example.mygrowth.domain.user.dto.UserProfileUpdateRequestDto;
import com.example.mygrowth.domain.user.dto.UserProfileResponseDto;
import com.example.mygrowth.domain.user.service.UserService;
import com.example.mygrowth.global.config.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name="User", description = "유저 정보 관련")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    /**
     * 유저 프로필 조회 API
     * @param customUserDetails 로그인한 유저
     * @return 유저 정보 dto
     */
    @GetMapping("/profile")
    @Operation(summary="유저 정보 조회", description = "로그인한 유저의 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "유저 정보 조회 성공")
    public ResponseEntity<UserProfileResponseDto> findUserProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        UserProfileResponseDto responseDto = userService.findUserProfile(customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 유저 프로필 수정 API
     * @param requestDto 수정 dto
     * @param customUserDetails 로그인 유저
     * @return 유저 정보 dto
     */
    @PatchMapping("/profile")
    @Operation(summary="유저 프로필 수정", description = "로그인한 유저의 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "유저 정보 수정 성공")
    public ResponseEntity<UserProfileResponseDto> updateProfile(
            @RequestBody @Schema(implementation = UserProfileUpdateRequestDto.class) UserProfileUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        UserProfileResponseDto responseDto = userService.updateProfile(requestDto, customUserDetails.getUser());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
