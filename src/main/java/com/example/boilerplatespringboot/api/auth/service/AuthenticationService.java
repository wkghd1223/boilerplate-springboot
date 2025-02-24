package com.example.boilerplatespringboot.api.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import com.example.boilerplatespringboot.api.auth.dto.AuthenticationDto;
import com.example.boilerplatespringboot.api.auth.dto.TokenDetails;
import com.example.boilerplatespringboot.api.auth.dto.UserDto;
import com.example.boilerplatespringboot.api.auth.entity.UserEntity;
import com.example.boilerplatespringboot.api.auth.repository.UserRepository;
import com.example.boilerplatespringboot.api.common.service.EncryptService;
import com.example.boilerplatespringboot.common.enums.ApiStatus;
import com.example.boilerplatespringboot.common.enums.Role;
import com.example.boilerplatespringboot.common.exception.ApiException;
import com.example.boilerplatespringboot.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final EncryptService encryptService;

  @Transactional
  public void signUp(AuthenticationDto.SignUpRequest dto) {
    UserEntity entity = new UserEntity();

    entity.setNm(dto.getName());
    entity.setUsrnm(dto.getUsername());
    entity.setPwd(passwordEncoder.encode(dto.getPassword()));
    entity.setRole(Role.ROLE_ADMISSION_OFFICER);

    userRepository.save(entity);
  }

  @Transactional
  public AuthenticationDto.JwtResponse signIn(AuthenticationDto.SignInRequest dto) {
    String username = dto.getUsername();
    String password = dto.getPassword();

    UserEntity userEntity = userRepository.findByUsrnm(username)
        .orElseThrow(() -> new ApiException(ApiStatus.RESOURCE_NOT_FOUND));

    TokenDetails tokenDetails = TokenDetails.from(userEntity);
    tokenDetails.setPassword(password);

    Authentication authentication = authenticate(tokenDetails);

    AuthenticationDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

    UserDto.User user = UserDto.User.from(userEntity);

    return AuthenticationDto.JwtResponse.builder()
        .user(user)
        .token(tokenInfo)
        .build();
  }

  private Authentication authenticate(TokenDetails tokenDetails) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
              tokenDetails.getUsername(),
              tokenDetails.getPassword(),
              tokenDetails.getAuthorities()
          ));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      return authentication;
    } catch (DisabledException ex) {
      log.error("USER_DISABLED", ex);
      throw new ApiException(ApiStatus.METHOD_ARGUMENT_NOT_VALID, "올바른 사용자가 아닙니다.");
    } catch (BadCredentialsException ex) {
      log.error("BAD_CREDENTIALS", ex);
      throw new ApiException(ApiStatus.CONSTRAINT_VIOLATION, "비밀번호가 올바르지 않습니다.");
    }
  }

  @Transactional
  public AuthenticationDto.JwtResponse reissue(AuthenticationDto.TokenInfo dto) {
    String accessToken = dto.getAccessToken();
    String refreshToken = dto.getRefreshToken();
    // 유저 조회
    String username = jwtTokenProvider.getUsername(accessToken);
    UserEntity userEntity = userRepository.findByUsrnm(username)
        .orElseThrow(() -> new ApiException(ApiStatus.RESOURCE_NOT_FOUND));

    try {
      jwtTokenProvider.validateToken(refreshToken);
    } catch (ExpiredJwtException e) {
      // Both refresh and access token is invalidated.
      throw new ApiException(ApiStatus.EXPIRED_REFRESH_TOKEN);
    } catch (Exception e) {
      // 리프레시 토큰이 만료된 것 이외에 다른 오류들은 다른 오류처리
      // All errors except validated refresh token are ...
      throw new ApiException(ApiStatus.EXPIRED_REFRESH_TOKEN, "토큰값이 잘못되었습니다.");
    }
    // 엑세스 토큰 && 리프레시 토큰 재생성
    AuthenticationDto.TokenInfo tokenInfo = jwtTokenProvider.generateReAccessToken(accessToken, refreshToken);

    UserDto.User user = UserDto.User.from(userEntity);

    return AuthenticationDto.JwtResponse.builder()
        .user(user)
        .token(tokenInfo)
        .build();
  }

  public void delete(Long userId) {
    userRepository.deleteById(userId);
  }
}
