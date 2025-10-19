package com.example.demo.service;

import com.example.demo.auth.UserRegisterRequestDto;
import com.example.demo.domain.User;
import com.example.demo.exception.EmailAlreadyExistsException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public User registerNewUser(UserRegisterRequestDto requestDto) {
        // 이메일 중복 검사
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("이미 사용 중인 이메일입니다: " + requestDto.getEmail());
        }

        try {
            User user = new User();
            user.setEmail(requestDto.getEmail());
            user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
            user.setName(requestDto.getName());
            user.setRole("USER"); // Default role

            User savedUser = userRepository.save(user);
            
            // 환영 이메일 발송 (비동기로 처리하여 회원가입에 영향 없도록)
            try {
                emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getName());
            } catch (Exception emailException) {
                // 이메일 발송 실패는 로그만 남기고 회원가입은 성공으로 처리
                System.err.println("환영 이메일 발송 실패: " + emailException.getMessage());
            }
            
            return savedUser;
        } catch (Exception e) {
            throw new RuntimeException("회원가입 처리 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일이 입력되지 않았습니다.");
        }
        
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("사용자 ID가 입력되지 않았습니다.");
        }
        
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + id));
    }

    @Transactional(readOnly = true)
    public java.util.List<User> getUsers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return userRepository.findAll();
        }
        return userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(searchTerm, searchTerm);
    }

    @Transactional
    public User updateUser(Long userId, com.example.demo.user.UserDto userDto) {
        User user = findById(userId);

        // 이름, 역할, 나이, 직업 변경 허용
        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            user.setName(userDto.getName());
        }
        if (userDto.getRole() != null && !userDto.getRole().isEmpty()) {
            user.setRole(userDto.getRole());
        }
        if (userDto.getAge() > 0) {
            user.setAge(userDto.getAge());
        }
        if (userDto.getOccupation() != null && !userDto.getOccupation().isEmpty()) {
            user.setOccupation(userDto.getOccupation());
        }

        // @Transactional에 의해 메서드 종료 시 자동으로 저장됨
        return user;
    }

    @Transactional
    public void updateUserEnabledStatus(Long userId, boolean enabled) {
        User user = findById(userId);
        user.setEnabled(enabled);
    }

    @Transactional
    public User createUserByAdmin(UserRegisterRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("이미 사용 중인 이메일입니다: " + requestDto.getEmail());
        }

        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setName(requestDto.getName());
        user.setRole(requestDto.getRole() != null ? requestDto.getRole() : "USER");
        user.setEnabled(true);

        return userRepository.save(user);
    }
}
