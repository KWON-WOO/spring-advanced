package org.example.expert.config;

import io.jsonwebtoken.Claims;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String bearer;
    @BeforeEach
    void setUp(){
        jwtUtil = new JwtUtil();
        String secretKey = Base64.getEncoder().encodeToString("sdfgsadfaerw42534redfsdfaserw4534fsdf".getBytes());
        bearer = (String)ReflectionTestUtils.getField(jwtUtil, "BEARER_PREFIX");
        ReflectionTestUtils.setField(jwtUtil, "secretKey", secretKey);
        jwtUtil.init();
    }

    @Test
    @DisplayName("토큰 생성 시 username, role 정보 포함 여부")
    void createTokenTest() {
        Long id = 1L;
        String email = "test1@google.com";
        UserRole role = UserRole.ADMIN;

        String jwtToken = jwtUtil.createToken(id, email, role);
        String token = jwtUtil.substringToken(jwtToken);


        Claims claims = jwtUtil.extractClaims(token);

        assertThat(claims.getSubject()).isEqualTo(String.valueOf(id));
        assertThat(claims.get("email", String.class)).isEqualTo(email);
        assertThat(claims.get("userRole",String.class)).isEqualTo(role.toString());


//        String jwt = jwtToken.substring("Bearer ".length());


    }

    @Test
    void substringToken_잘못된_형식_주입_시_예외_발생() {
        String tokenValue = "아무런 데이터";
        CustomException exception = assertThrows(CustomException.class, () -> {
            jwtUtil.substringToken(tokenValue);
        });

        assertEquals("Not found Token",exception.getMessage());
    }
}
