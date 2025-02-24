package com.example.boilerplatespringboot.api.common.mapper;

import com.example.boilerplatespringboot.api.common.dto.FileDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FileMapper {
  FileDto.Detail getFileDetail(@Param("fileId") String fileId, @Param("fileSn") Integer fileSn);

  void deleteFileDetail(FileDto.Detail dto);
}
