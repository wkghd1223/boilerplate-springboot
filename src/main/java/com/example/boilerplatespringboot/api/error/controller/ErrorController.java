package com.example.boilerplatespringboot.api.error.controller;

import com.example.boilerplatespringboot.common.dto.ApiResponseDto;
import com.example.boilerplatespringboot.common.enums.ApiStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.boilerplatespringboot.common.Constants.YYYYMMDDHHMMSS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/error")
public class ErrorController {

  @GetMapping
  public ApiResponseDto.ErrorResponse healthCheck() {
    return ApiResponseDto.ErrorResponse.builder()
        .status(ApiStatus.NO_HANDLER_FOUND.getCode())
        .message(ApiStatus.NO_HANDLER_FOUND.getMessage())
        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS)))
        .build();
  }
}
