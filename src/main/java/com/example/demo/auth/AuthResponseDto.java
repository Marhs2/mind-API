package com.example.demo.auth;

import com.example.demo.user.UserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDto {
    private String accessToken;
    private UserDto user;
}
