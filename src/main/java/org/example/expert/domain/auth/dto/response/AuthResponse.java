package org.example.expert.domain.auth.dto.response;

import lombok.Getter;

@Getter
public class AuthResponse {
    private final Long id;
    private final String bearerToken;

    public AuthResponse(Long id, String bearerToken) {
        this.id = id;
        this.bearerToken = bearerToken;
    }
}
