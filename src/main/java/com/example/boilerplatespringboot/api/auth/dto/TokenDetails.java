package com.example.boilerplatespringboot.api.auth.dto;

import com.example.boilerplatespringboot.api.auth.entity.UserEntity;
import com.example.boilerplatespringboot.common.enums.AdminAuthority;
import com.example.boilerplatespringboot.common.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static com.example.boilerplatespringboot.common.enums.AdminAuthority.*;

@Getter
@Setter
@Builder
public class TokenDetails implements UserDetails, Serializable {

  private Long userId;
  private String username;
  private Role role;
  private Collection<? extends GrantedAuthority> authorities;
  private String password;

  public static TokenDetails from(UserDto.User user) {
    return TokenDetails.builder()
        .userId(user.getUserId())
        .username(user.getUsername())
        .role(user.getRole())
        .authorities(Role.ROLE_ADMIN.equals(user.getRole()) ?
            List.of(new AdminAuthority[]{MANAGE_UNIVERSITIES, MANAGE_ADMISSIONS, MANAGE_ACHIEVEMENTS}) :
            null
        )
        .password(user.getPassword())
        .build();
  }
  public static TokenDetails from(UserEntity entity) {
    return TokenDetails.from(UserDto.User.from(entity));
  }

  public static boolean isNotAdmin(TokenDetails tokenDetails) {
    return tokenDetails == null || !Role.ROLE_ADMIN.equals(tokenDetails.getRole());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Role.ROLE_ADMIN.equals(this.role) ?
            List.of(new AdminAuthority[]{MANAGE_UNIVERSITIES, MANAGE_ADMISSIONS, MANAGE_ACHIEVEMENTS}) :
            null;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }
}
