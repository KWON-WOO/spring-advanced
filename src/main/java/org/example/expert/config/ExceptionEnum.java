package org.example.expert.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionEnum {
    /** UNAUTHORIZED */
    AUTH_EXCEPTION(HttpStatus.UNAUTHORIZED,"@Auth와 AuthUser 타입은 함께 사용되어야 합니다."),
    MISMATCH_PASSWORD(HttpStatus.UNAUTHORIZED, "잘못된 비밀번호입니다."),

    /** INTERNAL_SERVER_ERROR */
    NOT_FOUND_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "Not found Token"),
    NOT_FOUND_WEATHER(HttpStatus.INTERNAL_SERVER_ERROR,"날씨 데이터가 없습니다."),
    WEATHER_DATA_LOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,"날씨 데이터를 가져오는데 실패했습니다. 상태 코드: "),

    /** BAD_REQUEST */
    MISMATCH_AUTHORIZED_TODO(HttpStatus.BAD_REQUEST,"일정을 생성한 유저만 담당자를 지정할 수 있습니다."),
    NOT_FOUND_TODO(HttpStatus.BAD_REQUEST, "Todo not found"),
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST,"User not found"),
    NOT_FOUND_MANAGER(HttpStatus.BAD_REQUEST, "Manager not found"),
    NOT_FOUND_EMAIL(HttpStatus.BAD_REQUEST, "존재하지 않는 이메일입니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST,"이미 존재하는 이메일입니다."),
    DUPLICATED_PASSWORD(HttpStatus.BAD_REQUEST, "새 비밀번호는 기존 비밀번호와 같을 수 없습니다."),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "유효하지 않은 UerRole"),
    INVALID_USER_OF_TODO(HttpStatus.BAD_REQUEST,"해당 일정을 만든 유저가 유효하지 않습니다."),
    INVALID_MANAGER(HttpStatus.BAD_REQUEST,"해당 일정에 등록된 담당자가 아닙니다."),
    INVALID_CREATOR_ASSIGNMENT(HttpStatus.BAD_REQUEST, "일정 작성자는 본인을 담당자로 등록할 수 없습니다.")
    ;
    private final HttpStatus status;
    private final String message;
}
