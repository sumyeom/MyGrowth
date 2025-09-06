package com.example.mygrowth.domain.user.controller;


import com.example.mygrowth.domain.user.dto.UserProfileUpdateRequestDto;
import com.example.mygrowth.domain.user.dto.UserProfileResponseDto;
import com.example.mygrowth.domain.user.service.UserService;
import com.example.mygrowth.global.config.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> findUserProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        UserProfileResponseDto responseDto = userService.findUserProfile(customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PatchMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> updateProfile(
            @RequestBody UserProfileUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        UserProfileResponseDto responseDto = userService.updateProfile(requestDto, customUserDetails.getUser());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
