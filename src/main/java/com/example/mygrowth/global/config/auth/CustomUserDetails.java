package com.example.mygrowth.global.config.auth;

import com.example.mygrowth.domain.user.entity.User;
import com.example.mygrowth.domain.user.enums.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@RequiredArgsConstructor
@Slf4j(topic = "Security::CustomUserDetails")
public class CustomUserDetails implements UserDetails {

    private final User user;

    /**
     * 계정의 권한 리스트를 리턴
     * @return {@code Collection<? extends GrantedAuthority>}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        return authorities;
    }

    /**
     * 사용자의 비밀번호
     *
     * @return 암호
     */
    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    /**
     * 사용자 이름
     *
     * @return 사용자 이름
     */
    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    /**
     * 계정 만료
     *
     * @return 사용 여부
     * @apiNote 사용하지 않을 경우 true를 리턴하도록 재정의.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠금.
     *
     * @return 사용 여부
     * @apiNote 사용하지 않을 경우 true를 리턴하도록 재정의
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 자격 증명 만료
     *
     * @return 사용 여부
     * @apiNote 사용하지 않을 경우 true를 리턴하도록 재정의
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 활성화
     *
     * @return 사용 여부
     * @apiNote 사용하지 않을 경우 true를 리턴하도록 재정의
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
