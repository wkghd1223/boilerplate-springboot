package com.example.boilerplatespringboot.api.common.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import com.example.boilerplatespringboot.api.common.dto.FileDto;
import com.example.boilerplatespringboot.api.common.service.EncryptService;
import com.example.boilerplatespringboot.api.common.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Tag(name = "File", description = "파일관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/files")
public class FileController {

  private final FileService fileService;
  private final EncryptService encryptService;

  @Tag(name = "File")
  @Operation(summary = "파일 다운로드", description = "파일다운로드 기능 제공" )
  @GetMapping(value = "/{fileId}", produces = {
      MediaType.APPLICATION_OCTET_STREAM_VALUE,
      MediaType.APPLICATION_PDF_VALUE,
      MediaType.IMAGE_JPEG_VALUE,
      MediaType.IMAGE_JPEG_VALUE,
      MediaType.IMAGE_GIF_VALUE
  })
  public ResponseEntity<byte[]> getFile(
      HttpServletRequest request,
      @Parameter(
          description = "파일 아이디",
          required = true,
          schema = @Schema(example = "RKpAjSlRgVcn-LI_JLtAOgefkpjTsKmWZ9GrpIztkso")
      )
      @PathVariable String fileId,
      @Parameter(
          description = "파일 시리얼 넘버",
          schema = @Schema(example = "1")
      )
      @RequestParam(value = "fileSn", required = false) Integer fileSn
  ) {
    FileDto.Request dto = new FileDto.Request();
    dto.setFileId(encryptService.decryptString(fileId));
    dto.setFileSn(fileSn == null ? 1 : fileSn);
    return fileService.getFile(request, dto);
  }

  @Tag(name = "File")
  @Operation(summary = "이미지 미리보기", description = "첨부된 이미지에 대한 미리보기 기능을 제공" )
  @GetMapping(value = "/images/{fileId}", produces = { MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE })
  public ResponseEntity<byte[]> getImage(
      HttpServletRequest request,
      @Parameter(
          description = "파일 아이디",
          required = true,
          schema = @Schema(example = "RKpAjSlRgVcn-LI_JLtAOgefkpjTsKmWZ9GrpIztkso")
      )
      @PathVariable String fileId,
      @Parameter(
          description = "파일 시리얼 넘버",
          schema = @Schema(example = "1")
      )
      @RequestParam("fileSn") Integer fileSn
  ) {
    FileDto.Request dto = new FileDto.Request();
    dto.setFileId(encryptService.decryptString(fileId));
    dto.setFileSn(fileSn == null ? 1 : fileSn);
    return fileService.getFile(request, dto);
  }

  @Tag(name = "File")
  @Operation(summary = "파일저장", description = "파일 저장" )
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public FileDto.Response save(final @RequestParam("files") List<MultipartFile> files) {
    FileDto.Response res = fileService.save(files, null, null);
    res.setFileId(encryptService.encryptString(res.getFileId()));
    res.setFiles(encryptService.encryptFileBaseList(res.getFiles()));
    return res;
  }

  @Tag(name = "File")
  @Operation(summary = "파일저장", description = "파일 저장" )
  @PostMapping(value = "/{fileId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public FileDto.Response save(
      final @RequestParam("files") List<MultipartFile> files,
      @Parameter(
          description = "파일 아이디",
          schema = @Schema(example = "RKpAjSlRgVcn-LI_JLtAOgefkpjTsKmWZ9GrpIztkso")
      )
      @PathVariable String fileId
  ) {
    FileDto.Response res = fileService.save(files, null, encryptService.decryptString(fileId));
    res.setFileId(encryptService.encryptString(res.getFileId()));
    res.setFiles(encryptService.encryptFileBaseList(res.getFiles()));
    return res;
  }

  @Tag(name = "File")
  @Operation(summary = "파일삭제", description = "파일 삭제" )
  @DeleteMapping(value = "/{fileId}/{fileSn}")
  public void delete(
      @Parameter(
          description = "파일 아이디",
          schema = @Schema(example = "RKpAjSlRgVcn-LI_JLtAOgefkpjTsKmWZ9GrpIztkso")
      )
      @PathVariable String fileId,
      @Parameter(
          description = "파일 시리얼 넘버",
          schema = @Schema(example = "1")
      )
      @PathVariable Integer fileSn
  ) {
    fileService.deleteFile(encryptService.decryptString(fileId), fileSn);
  }
}
