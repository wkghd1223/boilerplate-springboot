package com.example.boilerplatespringboot.api.auth.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.example.boilerplatespringboot.api.auth.dto.AuthenticationDto;
import com.example.boilerplatespringboot.api.auth.dto.TokenDetails;
import com.example.boilerplatespringboot.api.auth.service.AuthenticationService;
import com.example.boilerplatespringboot.common.enums.ApiStatus;
import com.example.boilerplatespringboot.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "사용자인증")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  @Tag(name = "Authentication")
  @Operation(
      summary = "회원가입 API",
      description = "회원가입 API"
  )
  @PostMapping("/sign-up")
  public void signUp(@RequestBody @Valid AuthenticationDto.SignUpRequest dto) {
    authenticationService.signUp(dto);
  }

  @Tag(name = "Authentication")
  @Operation(
      summary = "로그인 API",
      description = "로그인 API"
  )
  @PostMapping("/sign-in")
  public AuthenticationDto.JwtResponse signIn(@RequestBody @Valid AuthenticationDto.SignInRequest dto) {
    return authenticationService.signIn(dto);
  }

  @Tag(name = "Authentication")
  @Operation(
      summary = "토큰 재발급 API",
      description = """
          1. 엑세스토큰이 만료되었다면 리프레시토큰 검사\s
          2. 리프레시 토큰이 만료되지 않았다면 토큰 재발급\s
          3. 토큰 리턴"""
  )
  @PostMapping("/reissue")
  public AuthenticationDto.JwtResponse reissue(@RequestBody @Valid AuthenticationDto.TokenInfo dto) {
    return authenticationService.reissue(dto);
  }

  // TODO: User 만들어서 그쪽으로 옮길 지 고민 좀
  @Tag(name = "Authentication")
  @Operation(
      summary = "탈퇴 API",
      description = "탈퇴 API",
      security = {@SecurityRequirement(name = "Authorization")}
  )
  @DeleteMapping("/account")
  public void delete(@AuthenticationPrincipal TokenDetails tokenDetails) {
    if (tokenDetails == null) {
      throw new ApiException(ApiStatus.UNAUTHORIZED);
    }
    Long userId = tokenDetails.getUserId();
    authenticationService.delete(userId);
  }
}
