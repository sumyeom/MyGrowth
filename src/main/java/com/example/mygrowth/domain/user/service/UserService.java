package com.example.mygrowth.domain.user.service;


import com.example.mygrowth.domain.user.dto.UserProfileUpdateRequestDto;
import com.example.mygrowth.domain.user.dto.UserProfileResponseDto;
import com.example.mygrowth.domain.user.entity.User;
import com.example.mygrowth.domain.user.repository.UserRepository;
import com.example.mygrowth.global.constant.ErrorCode;
import com.example.mygrowth.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserProfileResponseDto findUserProfile(User loginUser) {
        User user = userRepository.findByEmail(loginUser.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        return new UserProfileResponseDto(
                user.getName(),
                user.getNickname(),
                user.getSelfIntroduction()
        );
    }

    @Transactional
    public UserProfileResponseDto updateProfile(UserProfileUpdateRequestDto requestDto, User loginUser) {

        User user = userRepository.findByEmail(loginUser.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if(requestDto.getName()!=null){
            user.updateName(requestDto.getName());
        }

        if(requestDto.getNickname()!=null){
            user.updateNickname(requestDto.getNickname());
        }

        if(requestDto.getSelfIntroduction()!=null){
            user.updateIntroduction(requestDto.getSelfIntroduction());
        }

        return new UserProfileResponseDto(
                user.getName(),
                user.getNickname(),
                user.getSelfIntroduction()
        );
    }


}
