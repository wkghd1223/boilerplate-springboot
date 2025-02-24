package com.example.boilerplatespringboot.api.health.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.boilerplatespringboot.api.auth.dto.TokenDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health Check", description = "헬스체크")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/health-check")
public class HealthCheckController {

  @Tag(name = "Health Check")
  @Operation(
      summary = "상태체크 API",
      description = "상태체크 API",
      security = {@SecurityRequirement(name = "Authorization")}
  )
  @GetMapping
  public void healthCheck(@AuthenticationPrincipal TokenDetails tokenDetails) {
    log.info("헬스체크");
  }
}
