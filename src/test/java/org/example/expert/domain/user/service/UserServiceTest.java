package org.example.expert.domain.user.service;

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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
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
}
