package com.example.boilerplatespringboot.api.notice.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.example.boilerplatespringboot.api.auth.dto.TokenDetails;
import com.example.boilerplatespringboot.api.notice.dto.NoticeDto;
import com.example.boilerplatespringboot.api.notice.service.NoticeService;
import com.example.boilerplatespringboot.common.enums.ApiStatus;
import com.example.boilerplatespringboot.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.boilerplatespringboot.api.auth.dto.TokenDetails.isNotAdmin;

@Tag(name = "Notice", description = "공지사항")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/notices")
public class AdminNoticeController {

  private final NoticeService noticeService;

  @Tag(name = "Notice")
  @Operation(
      summary = "공지사항 저장 API",
      description = "공지사항 저장 API",
      security = {@SecurityRequirement(name = "Authorization")}
  )
  @PostMapping
  public void save(
      @AuthenticationPrincipal TokenDetails tokenDetails,
      @Valid @RequestBody NoticeDto.Create dto
  ) {
    if (isNotAdmin(tokenDetails)) {
      throw new ApiException(ApiStatus.UNAUTHORIZED);
    }
    dto.setAuthorId(tokenDetails.getUserId());
    noticeService.save(dto);
  }

  @Tag(name = "Notice")
  @Operation(
      summary = "공지사항 수정 API",
      description = "공지사항 수정 API",
      security = {@SecurityRequirement(name = "Authorization")}
  )
  @PutMapping("/{noticeId}")
  public void update(
      @AuthenticationPrincipal TokenDetails tokenDetails,
      @Parameter(
          description = "공지사항 아이디",
          required = true,
          schema = @Schema(example = "1")
      )
      @PathVariable Long noticeId,
      @Valid @RequestBody NoticeDto.Update dto
  ) {
    if (isNotAdmin(tokenDetails)) {
      throw new ApiException(ApiStatus.UNAUTHORIZED);
    }
    dto.setNoticeId(noticeId);
    noticeService.update(dto);
  }

  @Tag(name = "Notice")
  @Operation(
      summary = "공지사항 삭제 API",
      description = "공지사항 삭제 API",
      security = {@SecurityRequirement(name = "Authorization")}
  )
  @DeleteMapping("/{noticeId}")
  public void delete(
      @AuthenticationPrincipal TokenDetails tokenDetails,
      @Parameter(
          description = "공지사항 아이디",
          required = true,
          schema = @Schema(example = "1")
      )
      @PathVariable Long noticeId
  ) {
    if (isNotAdmin(tokenDetails)) {
      throw new ApiException(ApiStatus.UNAUTHORIZED);
    }
    noticeService.delete(noticeId);
  }
}
