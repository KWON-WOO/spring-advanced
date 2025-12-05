package org.example.expert.domain.auth.service;

import org.example.expert.config.CustomException;
import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.AuthResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;

    private User testUser;
    @BeforeEach
    void setup() {
        testUser = new User("test@test.com", "Password1234", UserRole.USER);
    }
    @Test
    void signupTest() {
        SignupRequest request = new SignupRequest("test@test.com", "A12341234", UserRole.USER.toString());

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.createToken(testUser.getId(), testUser.getEmail(), testUser.getUserRole())).thenReturn("Bearer String");

        String token = jwtUtil.createToken(testUser.getId(), testUser.getEmail(), testUser.getUserRole());

        AuthResponse response = authService.signup(request);

        assertThat(response.getId()).isEqualTo(testUser.getId());
        assertThat(response.getBearerToken().substring(0,7)).isEqualTo(token.substring(0,7));

    }

    @Test
    void signinTest() {
        Long id = 1L;
        SigninRequest request = new SigninRequest("test@test.com", "Password1234");

        when(passwordEncoder.matches(request.getPassword(),testUser.getPassword())).thenReturn(true);
        when(jwtUtil.createToken(testUser.getId(), testUser.getEmail(), testUser.getUserRole())).thenReturn("Bearer String");
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(testUser));

        String token = jwtUtil.createToken(testUser.getId(), testUser.getEmail(), testUser.getUserRole());

        AuthResponse response = authService.signin(request);

        assertThat(response.getId()).isEqualTo(testUser.getId());
        assertThat(response.getBearerToken().substring(0,7)).isEqualTo(token.substring(0,7));

    }

    @Test
    void signinTest_이메일_틀렸을_시_에러_반환(){
        Long id = 1L;
        SigninRequest request = new SigninRequest("test11@test.com", "Password1234");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            authService.signin(request);
        });
        assertEquals("존재하지 않는 이메일입니다.", exception.getMessage());
    }
    @Test
    void signinTest_비밀번호_틀렸을_시_에러_반환(){
        Long id = 1L;
        SigninRequest request = new SigninRequest("test@test.com", "Password12345");
        when(passwordEncoder.matches(request.getPassword(),testUser.getPassword())).thenReturn(false);
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(testUser));

        CustomException exception = assertThrows(CustomException.class, () -> {
            authService.signin(request);
        });
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }
}