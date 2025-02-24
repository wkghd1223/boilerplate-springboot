package com.example.boilerplatespringboot.api.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import com.example.boilerplatespringboot.api.auth.entity.UserEntity;
import com.example.boilerplatespringboot.common.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class UserDto {

  @Getter
  @Setter
  @Builder
  @Schema(name = "유저 Dto")
  public static class User {
    @JsonIgnore
    private Long userId;
    @Schema(description = "아이디", example = "htj")
    private String username;
    @Schema(description = "이름", example = "김철수")
    private String name;
    @Schema(description = "역할", example = "ROLE_ADMISSION_OFFICER")
    private Role role;
    @JsonIgnore
    private String password;

    public static User from(UserEntity entity) {
      return User.builder()
          .userId(entity.getUsrId())
          .username(entity.getUsrnm())
          .name(entity.getNm())
          .password(entity.getPwd())
          .role(entity.getRole())
          .build();
    }
  }

}
