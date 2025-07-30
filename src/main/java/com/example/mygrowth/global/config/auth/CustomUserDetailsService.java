package com.example.mygrowth.global.config.auth;

import com.example.mygrowth.domain.user.entity.User;
import com.example.mygrowth.domain.user.repository.UserRepository;
import com.example.mygrowth.global.constant.ErrorCode;
import com.example.mygrowth.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j(topic = "Security::CustomUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    /**
     * User entity의 Repository
     */
    private final UserRepository userRepository;

    /**
     * 입력받은 이메일에 해당하는 사용자 정보를 찾아 리턴.
     *
     * @param username username
     * @return 해당하는 사용자의 {@link CustomUserDetails} 객체
     * @throws UsernameNotFoundException 이메일에 해당하는 사용자를 찾지 못한 경우
     * @apiNote 이 애플리케이션에서는 사용자의 이메일을 {@code username}으로 사용합니다
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmail(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        log.info("findUser : {}", username);
        return new CustomUserDetails(user);
    }
}
