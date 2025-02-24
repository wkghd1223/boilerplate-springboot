package com.example.boilerplatespringboot.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.boilerplatespringboot.api.auth.entity.UserEntity;
import com.example.boilerplatespringboot.api.auth.repository.UserRepository;
import com.example.boilerplatespringboot.common.enums.ApiStatus;
import com.example.boilerplatespringboot.common.exception.ApiException;
import com.example.boilerplatespringboot.common.exception.JwtAuthenticationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static com.example.boilerplatespringboot.common.Constants.*;

/**
 * HTTP 요청을 중간에서 가로채어 JWT를 처리하고, 사용자를 인증함으로써 SecurityContextHolder에 해당 인증 정보를 설정하는 역할.
 */
@Slf4j
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private static final Set<String> PATHS = Set.of("/api/v1/auth/sign-in", "/api/v1/auth/reissue");

    private JwtTokenProvider jwtTokenProvider;
    private AuthenticationEntryPoint entryPoint;
    private UserRepository userRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return PATHS.contains(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = getToken(request);
        if (token != null) {
            try {
                jwtTokenProvider.validateToken(token);

                String username = jwtTokenProvider.getUsername(token);

                UserEntity entity = userRepository.findByUsrnm(username)
                    .orElseThrow(() -> new ApiException(ApiStatus.RESOURCE_NOT_FOUND));

                Optional<PreAuthenticatedAuthenticationToken> authentication =
                    jwtTokenProvider.getAuthentication(entity);

                // spring security user password auth 방식 동작 시키기 위한 처리
                authentication.ifPresentOrElse(e -> SecurityContextHolder.getContext().setAuthentication(e),
                    SecurityContextHolder::clearContext);

                filterChain.doFilter(request, response);
            } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
                log.error("Invalid JWT Token", e);
                request.setAttribute(EXCEPTION_CODE, ApiStatus.UNSUPPORTED_JWT.getCode());
                entryPoint.commence(request, response,
                    new JwtAuthenticationException(ApiStatus.UNSUPPORTED_JWT, e));
            } catch (ExpiredJwtException e) {
                log.error("Expired JWT Token", e);
                request.setAttribute(EXCEPTION_CODE, ApiStatus.EXPIRED_ACCESS_TOKEN.getCode());
                entryPoint.commence(request, response,
                    new JwtAuthenticationException(ApiStatus.EXPIRED_ACCESS_TOKEN, e));
            } catch (UnsupportedJwtException e) {
                log.error("Unsupported JWT Token", e);
                request.setAttribute(EXCEPTION_CODE, ApiStatus.UNSUPPORTED_JWT.getCode());
                entryPoint.commence(request, response,
                    new JwtAuthenticationException(ApiStatus.UNSUPPORTED_JWT, e));
            } catch (IllegalArgumentException e) {
                log.error("JWT claims string is empty.", e);
                request.setAttribute(EXCEPTION_CODE, ApiStatus.UNSUPPORTED_JWT.getCode());
                entryPoint.commence(request, response,
                    new JwtAuthenticationException(ApiStatus.UNSUPPORTED_JWT, e));
            } catch (Exception e) {
                log.error("API exception", e);
                entryPoint.commence(request, response,
                    new JwtAuthenticationException(ApiStatus.RESOURCE_NOT_FOUND, e));
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    // 토큰값 문자열 리턴 메소드
    private String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}