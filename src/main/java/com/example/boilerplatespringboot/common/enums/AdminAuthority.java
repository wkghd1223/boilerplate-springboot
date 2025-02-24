package com.example.boilerplatespringboot.common.enums;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum AdminAuthority implements GrantedAuthority {

  // 관리자
  MANAGE_UNIVERSITIES("제휴대학관리"),
  MANAGE_ADMISSIONS("사정관관리"),
  MANAGE_ACHIEVEMENTS("대학별성취기준관리"),
  ;

  private final String authority;

  AdminAuthority(String authority) {
    this.authority = authority;
  }

  @Override
  public String getAuthority() {
    return null;
  }
}
