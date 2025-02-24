package com.example.boilerplatespringboot.config.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import com.example.boilerplatespringboot.api.auth.dto.AuthenticationDto;
import com.example.boilerplatespringboot.api.auth.dto.TokenDetails;
import com.example.boilerplatespringboot.api.auth.dto.UserDto;
import com.example.boilerplatespringboot.api.auth.entity.UserEntity;
import com.example.boilerplatespringboot.common.enums.ApiStatus;
import com.example.boilerplatespringboot.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.boilerplatespringboot.common.Constants.AUTH;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    @Value("${jwt.expire-time.access-token}")
    private long ACCESS_TOKEN_EXPIRE_TIME;
    @Value("${jwt.expire-time.refresh-token}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    public AuthenticationDto.TokenInfo generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        long now = new Date().getTime();

        //accessToken generate
        String accessToken = Jwts.builder().setSubject(authentication.getName())
            .claim(AUTH, authorities).setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME))
            .signWith(key, SignatureAlgorithm.HS256).compact();

        String refreshToken = Jwts.builder().setSubject(authentication.getName())
            .claim("check", authentication.getName())
            .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
            .signWith(key, SignatureAlgorithm.HS256).compact();

        return AuthenticationDto.TokenInfo.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    public AuthenticationDto.TokenInfo generateReAccessToken(String accessToken, String refreshToken) {
        Claims refreshTokenClaims = parseClaims(refreshToken);
        String accessTokenName;
        String accessTokenAuthorities;

        // 만료된 토큰의 claims 값 체크를 위한 예외 처리
        try {
            Claims accessTokenClaims = parseClaims(accessToken);
            accessTokenName = accessTokenClaims.getSubject();
            accessTokenAuthorities = accessTokenClaims.get(AUTH).toString();
        } catch (ExpiredJwtException e) {
            accessTokenName = e.getClaims().getSubject();
            accessTokenAuthorities = e.getClaims().get(AUTH).toString();
        }

        if (!accessTokenName.equals(refreshTokenClaims.get("check").toString())) {
            log.error("Token Not Match");
            throw new ApiException(ApiStatus.UNSUPPORTED_JWT);
        }

        long now = new Date().getTime();
        String newAccessToken = Jwts.builder().setSubject(accessTokenName)
            .claim(AUTH, accessTokenAuthorities).setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME))
            .signWith(key, SignatureAlgorithm.HS256).compact();
        String newRefreshToken = Jwts.builder().claim("check", accessTokenName)
            .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
            .signWith(key, SignatureAlgorithm.HS256).compact();

        return AuthenticationDto.TokenInfo.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .build();
    }


    public Optional<PreAuthenticatedAuthenticationToken> getAuthentication(UserDto.User user) {
        TokenDetails tokenDetails = TokenDetails.from(user);
        return Optional.of(
            new PreAuthenticatedAuthenticationToken(tokenDetails, null, tokenDetails.getAuthorities()));
    }

    public Optional<PreAuthenticatedAuthenticationToken> getAuthentication(UserEntity userEntity) {
        TokenDetails tokenDetails = TokenDetails.from(userEntity);
        return Optional.of(
            new PreAuthenticatedAuthenticationToken(tokenDetails, null, tokenDetails.getAuthorities()));
    }

    public String getUsername(String token) {
        String username;
        try {
            Claims claims = parseClaims(token);
            username = claims.getSubject();
        } catch (ExpiredJwtException e) {
            username = e.getClaims().getSubject();
        }
        return username;
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public void validateToken(String token) {
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token);
    }
}