package org.example.expert.domain.user.enums;

import org.example.expert.config.CustomException;
import org.example.expert.config.ExceptionEnum;
import java.util.Arrays;

public enum UserRole {
    ADMIN, USER;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new CustomException(ExceptionEnum.INVALID_USER_ROLE));
    }
}
