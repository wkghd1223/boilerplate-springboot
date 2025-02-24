package com.example.boilerplatespringboot.api.notice.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.boilerplatespringboot.api.notice.dto.NoticeDto;
import com.example.boilerplatespringboot.api.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notice", description = "공지사항")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/notices")
public class NoticeController {

  private final NoticeService noticeService;

  @Tag(name = "Notice")
  @Operation(
      summary = "공지사항 목록 API",
      description = "공지사항 목록 API"
  )
  @GetMapping
  public NoticeDto.ListResponse getList(@ParameterObject NoticeDto.ListRequest query) {
    return noticeService.getList(query);
  }

  @Tag(name = "Notice")
  @Operation(
      summary = "공지사항 상세조회 API",
      description = "공지사항 상세조회 API"
  )
  @GetMapping("/{noticeId}")
  public NoticeDto.Item getOne(
      @Parameter(
        description = "공지사항 아이디",
        required = true,
        schema = @Schema(example = "1")
      )
      @PathVariable Long noticeId
  ) {
    return noticeService.getOne(noticeId);
  }
}
