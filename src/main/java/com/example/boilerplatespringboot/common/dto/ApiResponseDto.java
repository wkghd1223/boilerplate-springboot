package com.example.boilerplatespringboot.common.dto;

import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import com.example.boilerplatespringboot.common.enums.ApiStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.example.boilerplatespringboot.common.Constants.YYYYMMDDHHMMSS;

public class ApiResponseDto {

  @Getter
  @Setter
  public static class List<T> {
    @Schema(description = "목록", requiredMode = Schema.RequiredMode.REQUIRED)
    private java.util.List<T> items;
    @Schema(description = "총 갯수")
    private int totalCount;
  }

  @Getter
  @Setter
  public static class Base<T> {

      private int status;
      private String message;
      private String timestamp;
      private T data;

      public Base() {
          this.status = 200;
          this.message = ApiStatus.OK.getMessage();
          this.timestamp = StringUtils.isNotBlank(timestamp) ? timestamp
              : LocalDateTime.now().format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
      }

      public Base(T data) {
          this();
          this.data = data;
      }
  }

  @Getter
  @Setter
  public static class ErrorResponse {

      private int status;
      private String message;
      private String timestamp;

      @Builder
      ErrorResponse(
          int status,
          String message,
          String timestamp
      ) {
        this.status = status;
        this.message =
            StringUtils.isNotBlank(message) ? message
                : ApiStatus.valueOfStatusCode(status).getMessage();
        this.timestamp =
            StringUtils.isNotBlank(timestamp) ? timestamp
                : LocalDateTime.now()
       .format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
      }

      public static ErrorResponse of(Map<String, ?> body) {
        return ErrorResponse.builder()
            .status(Integer.parseInt(((Integer) body.get("status")).toString()))
            .message((String) body.get("error"))
            .timestamp(LocalDateTime.now()
       .format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS)))
            .build();
      }
  }
}
