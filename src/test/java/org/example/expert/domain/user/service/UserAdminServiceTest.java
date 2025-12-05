package org.example.expert.domain.user.service;

import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserAdminServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserAdminService userService;

    @Test
    void changeUserRole() {
        Long id = 1L;
        User testUser = new User("test@test.com", "password", UserRole.USER);

        UserRoleChangeRequest request = new UserRoleChangeRequest(UserRole.USER.toString());

        when(userRepository.findById(id)).thenReturn(Optional.of(testUser));

        userService.changeUserRole(id, request);
    }
}
