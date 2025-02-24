package com.example.boilerplatespringboot.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class AuthenticationDto {

  @Getter
  @Setter
  @Builder
  @Schema(name = "회원가입 요청 Dto")
  public static class SignUpRequest {
    @Schema(description = "아이디", requiredMode = Schema.RequiredMode.REQUIRED, example = "htj")
    @Size(min = 1, max = 100, message = "아이디는 100자를 넘을 수 없습니다.")
    private String username;
    @Schema(description = "비밀번호", requiredMode = Schema.RequiredMode.REQUIRED, example = "htj1@")
    @Size(min = 1, max = 100, message = "비밀번호는 100자를 넘을 수 없습니다.")
    private String password;
    @Schema(description = "이름", requiredMode = Schema.RequiredMode.REQUIRED, example = "김철수")
    @Size(min = 1, max = 100, message = "이름은 100자를 넘을 수 없습니다.")
    private String name;
  }

  @Getter
  @Setter
  @Builder
  @Schema(name = "로그인 요청 Dto")
  public static class SignInRequest {
    @Schema(description = "아이디", requiredMode = Schema.RequiredMode.REQUIRED, example = "htj")
    @Size(min = 1, max = 100)
    private String username;
    @Schema(description = "비밀번호", requiredMode = Schema.RequiredMode.REQUIRED, example = "htj1@")
    @Size(min = 1, max = 100)
    private String password;
  }

  @Getter
  @Setter
  @Builder
  @Schema(name = "인증완료 응답 Dto")
  public static class JwtResponse {
    @Schema(description = "유저", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDto.User user;
    @Schema(description = "토큰정보", requiredMode = Schema.RequiredMode.REQUIRED)
    private TokenInfo token;
  }

  @Getter
  @Setter
  @Builder
  @Schema(name = "토큰정보 Dto")
  public static class TokenInfo {
    @Schema(description = "엑세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJodGoiLCJhdXRoIjoiIiwiZXhwIjoxNzQ5NTIyODg1fQ.HePVstmv7LV4mWWxCshyDWpo2JlEIfzvtPVAUWrSILk")
    private String accessToken;
    @Schema(description = "엑세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJodGoiLCJhdXRoIjoiIiwiZXhwIjoxNzQ5NTIyODg1fQ.HePVstmv7LV4mWWxCshyDWpo2JlEIfzvtPVAUWrSILk")
    private String refreshToken;
  }
}
