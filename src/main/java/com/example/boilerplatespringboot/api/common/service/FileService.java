package com.example.boilerplatespringboot.api.common.service;

import jakarta.servlet.http.HttpServletRequest;
import com.example.boilerplatespringboot.api.common.dto.FileDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
  public ResponseEntity<byte[]> getFile(HttpServletRequest request, FileDto.Request dto);

  public FileDto.Response save(List<MultipartFile> files, String path, String fileId);

  public void deleteFile(String fileId, Integer fileSn);
}
