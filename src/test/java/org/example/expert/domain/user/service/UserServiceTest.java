package org.example.expert.domain.user.service;

import jakarta.validation.Valid;
import org.example.expert.config.CustomException;
import org.example.expert.config.ExceptionEnum;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;
    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User("test@test.com", "A12341234", UserRole.USER);
        ReflectionTestUtils.setField(testUser, "id", 1L);
    }

    @Test
    @DisplayName("유저 불러오기")
    void getUserTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse userResponse = userService.getUser(1L);

        assertThat(userResponse.getId()).isEqualTo(testUser.getId());
        assertThat(userResponse.getEmail()).isEqualTo(testUser.getEmail());
    }
    @Test
    void changePasswordTest_유저를_찾을_수_없을_때(){
        UserChangePasswordRequest request = new UserChangePasswordRequest("Password1234", "Password12345");
        Long id = 10L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.changePassword(id, request);
        });
        assertEquals("User not found", exception.getMessage());
    }
    @Test
    void changePasswordTest(){
        UserChangePasswordRequest request = new UserChangePasswordRequest("Password1234", "Password12345");
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getNewPassword(), testUser.getPassword())).thenReturn(false);
        when(passwordEncoder.matches(request.getOldPassword(), testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("new password");
        userService.changePassword(id, request);
        assertEquals("new password", testUser.getPassword());
    }

    @Test
    void changePasswordTest_이전_비번과_일치할_때(){
        UserChangePasswordRequest request = new UserChangePasswordRequest("Password1234", "Password1234");
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getNewPassword(), testUser.getPassword())).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.changePassword(id, request);
        });
        assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
    }

    @Test
    void changePasswordTest_비밀번호가_일치하지_않을_때(){
        UserChangePasswordRequest request = new UserChangePasswordRequest("Password1234", "Password12345");
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getNewPassword(), testUser.getPassword())).thenReturn(false);
        when(passwordEncoder.matches(request.getOldPassword(), testUser.getPassword())).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.changePassword(id, request);
        });
        assertEquals(ExceptionEnum.MISMATCH_PASSWORD.getMessage(), exception.getMessage());
    }
}
