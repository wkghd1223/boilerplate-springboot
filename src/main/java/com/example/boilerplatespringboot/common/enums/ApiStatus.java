package com.example.boilerplatespringboot.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The enum Api status.
 */
@AllArgsConstructor
@Getter
public enum ApiStatus {
  OK(2000, "성공", HttpStatus.OK),

  // Custom Exception 에러 코드
  INVALID_REQUEST(4001, "유효하지 않은 요청입니다.", HttpStatus.BAD_REQUEST),
  DUPLICATED_REQUEST(4002, "중복된 요청입니다.", HttpStatus.BAD_REQUEST),
  METHOD_ARGUMENT_NOT_VALID(4003, "파라미터가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
  MISSING_SERVLET_REQUEST_PARAMETER(4004, "필수 파라미터가 누락되었습니다.", HttpStatus.BAD_REQUEST),
  CONSTRAINT_VIOLATION(4005, "파라미터 유효성 검사에 실패했습니다.", HttpStatus.BAD_REQUEST),
  METHOD_ARGUMENT_TYPE_MISMATCH(4006, "파라미터 타입이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
  HTTP_MESSAGE_NOT_READABLE_EXCEPTION(4007, "읽을 수 있는 요청 정보가 없습니다.", HttpStatus.BAD_REQUEST),

  FORBIDDEN_REQUEST(4030, "허용되지 않은 요청입니다.", HttpStatus.FORBIDDEN),
  HTTP_REQUEST_METHOD_NOT_SUPPORTED(4031, "지원하지 않는 메서드입니다.", HttpStatus.FORBIDDEN),
  HTTP_MEDIA_TYPE_NOT_SUPPORTED(4032, "지원되지 않는 미디어 타입입니다.", HttpStatus.FORBIDDEN),

  UNAUTHORIZED(4010, "유효하지 않은 권한입니다.", HttpStatus.UNAUTHORIZED),
  EXPIRED_ACCESS_TOKEN(4011, "토큰 유효 기간이 만료 되었습니다.", HttpStatus.UNAUTHORIZED),
  EXPIRED_REFRESH_TOKEN(4012, "토큰 유효 기간이 만료 되었습니다..", HttpStatus.UNAUTHORIZED),
  UNSUPPORTED_JWT(4013, "옳바르지 않는 토큰 입니다.", HttpStatus.UNAUTHORIZED),
  EMPTY_JWT(4014, "토큰정보가 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
  TOKEN_ERROR(4015, "토큰정보정보가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),

  NO_HANDLER_FOUND(4040, "요청한 URL을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  RESOURCE_NOT_FOUND(4041, "리소스가 존재하지 않는습니다.", HttpStatus.NOT_FOUND),

  INTERNAL_SERVER_ERROR(5000, "오류가 발생했습니다. 확인 후 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR),
  HTTP_INTERFACE_API_ERROR(5001, "외부 API 호출 오류 입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  ;

  private static final Map<Integer, ApiStatus> BY_STATUS_CODE =
      Stream.of(values())
          .collect(Collectors.toMap(ApiStatus::getCode, Function.identity()));
  private static final Map<String, ApiStatus> BY_MESSAGE =
      Stream.of(values())
          .collect(Collectors.toMap(ApiStatus::getMessage, Function.identity()));
  private final int code;
  private final String message;
  private final HttpStatus status;

  public static ApiStatus valueOfStatusCode(int code) {
    return BY_STATUS_CODE.get(code);
  }

  public static ApiStatus valueOfMessage(String message) {
    return BY_MESSAGE.get(message);
  }
}