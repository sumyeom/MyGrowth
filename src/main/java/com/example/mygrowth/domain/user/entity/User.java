package com.example.mygrowth.domain.user.entity;

import com.example.mygrowth.domain.routine.entity.Routine;
import com.example.mygrowth.domain.user.enums.Role;
import com.example.mygrowth.domain.user.enums.UserStatus;
import com.example.mygrowth.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @OneToMany(mappedBy = "user")
    private List<Routine> routines = new ArrayList<>();;


    public User(String email, String name, String nickname, String password){
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.role = Role.USER;
        this.userStatus = UserStatus.ACTIVE;

    }
}
