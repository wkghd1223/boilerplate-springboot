package com.example.boilerplatespringboot.api.notice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.example.boilerplatespringboot.common.Constants;
import com.example.boilerplatespringboot.common.dto.ApiRequestDto;
import com.example.boilerplatespringboot.common.dto.ApiResponseDto;
import lombok.*;

import java.time.LocalDateTime;

public class NoticeDto {


  @Getter
  @Setter
  @Builder
  @Schema(name = "공지사항 쿼리 Dto")
  public static class ListRequest extends ApiRequestDto.Query {
    private String title;
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Getter
  @Setter
  @Builder
  @Schema(name = "공지사항 목록조회 응답용 Dto")
  public static class ListItem {
    @Schema(description = "공지사항 아이디", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long noticeId;

    @Schema(description = "제목", requiredMode = Schema.RequiredMode.REQUIRED, example = "제목")
    private String title;

    @Schema(description = "생성일시", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-01-01 11:11:00", type = "string")
    @JsonFormat(pattern = Constants.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdAt;

    @Schema(description = "작성자명", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "김현태")
    private String authorName;
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Getter
  @Setter
  @Builder
  @Schema(name = "공지사항 상세조회 응답용 Dto")
  public static class Item {
    @Schema(description = "공지사항 아이디", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long noticeId;

    @Schema(description = "제목", requiredMode = Schema.RequiredMode.REQUIRED, example = "제목")
    private String title;

    @Schema(description = "내용", requiredMode = Schema.RequiredMode.REQUIRED, example = "내용")
    private String content;

    @Schema(description = "생성일시", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-01-01 11:11:00", type = "string")
    @JsonFormat(pattern = Constants.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdAt;

    @Schema(description = "작성자명", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "김현태")
    private String authorName;
  }

  @Getter
  @Setter
  @Schema(name = "공지사항 목록조회 응답 Dto")
  public static class ListResponse extends ApiResponseDto.List<ListItem> {
  }

  @Getter
  @Setter
  @Schema(name = "공지사항 생성 Dto")
  public static class Create {
    @Schema(hidden = true)
    private Long authorId;
    @Schema(description = "제목", requiredMode = Schema.RequiredMode.REQUIRED, example = "제목")
    @NotBlank
    @Size(max = 255)
    private String title;
    @Schema(description = "내용", requiredMode = Schema.RequiredMode.REQUIRED, example = "내용")
    @NotBlank
    private String content;
  }

  @Getter
  @Setter
  @Schema(name = "공지사항 업데이트 Dto")
  public static class Update {
    @Schema(hidden = true)
    private Long noticeId;
    @Schema(description = "제목", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "제목")
    @NotBlank
    @Size(max = 255)
    private String title;
    @Schema(description = "내용", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "내용")
    @NotBlank
    private String content;
  }
}
