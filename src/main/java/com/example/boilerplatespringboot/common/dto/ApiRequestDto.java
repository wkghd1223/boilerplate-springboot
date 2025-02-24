package com.example.boilerplatespringboot.common.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class ApiRequestDto {
  @Setter
  @Getter
  @Schema(name = "기본 쿼리")
  public static class Query {
    @Parameter(description = "페이지 번호", example = "0")
    @PositiveOrZero
    private int page = 0;
    @Parameter(description = "사이즈", example = "10")
    @Size(min = 1)
    private int size = 10;
  }
}
