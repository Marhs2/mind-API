package com.example.demo.auth;

import com.example.demo.common.ApiResponse;
import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import com.example.demo.user.UserDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody UserRegisterRequestDto request) {
        User registeredUser = userService.registerNewUser(request);
        UserDto userDto = new UserDto(registeredUser);
        ApiResponse<UserDto> response = ApiResponse.success("회원가입이 성공적으로 완료되었습니다.", userDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@Valid @RequestBody UserLoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userService.findByEmail(authentication.getName());
        String token = jwtUtil.generateToken(user);

        AuthResponseDto authResponse = new AuthResponseDto();
        authResponse.setAccessToken(token);
        authResponse.setUser(new UserDto(user));

        ApiResponse<AuthResponseDto> response = ApiResponse.success("로그인이 성공적으로 완료되었습니다.", authResponse);
        return ResponseEntity.ok(response);
    }
}