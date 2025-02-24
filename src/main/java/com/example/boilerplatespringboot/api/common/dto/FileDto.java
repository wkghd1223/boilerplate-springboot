package com.example.boilerplatespringboot.api.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.example.boilerplatespringboot.api.common.entity.FileDetailEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.util.List;

public class FileDto {

  @Getter
  @Setter
  @Schema(name = "파일저장 요청 DTO")
  public static class Request extends Base {
    @Schema(description = "파일 아이디(암호화됨)", requiredMode = Schema.RequiredMode.REQUIRED, example = "RKpAjSlRgVcn-LI_JLtAOgefkpjTsKmWZ9GrpIztkso")
    private String fileId;
    @Schema(description = "파일 시리얼 넘버", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer fileSn;
  }

  @Getter
  @Setter
  @Schema(name = "파일저장 응답 DTO")
  public static class Response {
    @Schema(description = "파일 아이디(암호화됨)", requiredMode = Schema.RequiredMode.REQUIRED, example = "RKpAjSlRgVcn-LI_JLtAOgefkpjTsKmWZ9GrpIztkso")
    private String fileId;
    @Schema(description = "파일 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Base> files;
  }

  @Alias("fileBase")
  @Getter
  @Setter
  @Schema(name = "파일상세 간략버전 DTO")
  public static class Base {

    @Schema(description = "파일 아이디(암호화됨)", requiredMode = Schema.RequiredMode.REQUIRED, example = "oO3rGEfD8twsMG5pYVeQOmMYJCSnIOFRw5yCxpeqW58=")
    private String fileId;
    @Schema(description = "파일 시리얼 넘버", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer fileSn;

    public static Base from(FileDetailEntity entity) {
      if (entity == null) {
        return null;
      }
      Base dto = new Base();
      dto.setFileSn(entity.getFileNum());
      dto.setFileId(entity.getFile().getFileId());
      return dto;
    }
  }

  @Alias("fileDetail")
  @Getter
  @Setter
  @Schema(name = "파일상세 DTO")
  public static class Detail extends Base {
    private String path;
    private String fileName;
    private String originalFileName;
    private String extension;
    private Integer size;
  }
}
