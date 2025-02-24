package com.example.boilerplatespringboot.api.common.service;

import jakarta.servlet.http.HttpServletRequest;
import com.example.boilerplatespringboot.api.common.dto.FileDto;
import com.example.boilerplatespringboot.api.common.entity.FileDetailEntity;
import com.example.boilerplatespringboot.api.common.entity.FileEntity;
import com.example.boilerplatespringboot.api.common.mapper.FileMapper;
import com.example.boilerplatespringboot.api.common.repository.FileDetailRepository;
import com.example.boilerplatespringboot.api.common.repository.FileRepository;
import com.example.boilerplatespringboot.common.enums.ApiStatus;
import com.example.boilerplatespringboot.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.example.boilerplatespringboot.common.Constants.YYYYMMDDHHMMSS;
import static com.example.boilerplatespringboot.common.utils.FileUtils.findContentType;
import static com.example.boilerplatespringboot.common.utils.FileUtils.getFileExtension;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalFileServiceInf implements FileService {
  private final FileMapper fileMapper;
  private final FileRepository fileRepository;
  private final FileDetailRepository fileDetailRepository;
  private final String BASE_PATH = "./files";

  @Transactional(readOnly = true)
  @Override
  public ResponseEntity<byte[]> getFile(HttpServletRequest request, FileDto.Request dto) {
    FileDto.Detail fileInfo = fileMapper.getFileDetail(dto.getFileId(), dto.getFileSn());
    if (fileInfo == null) {
      throw new ApiException(ApiStatus.RESOURCE_NOT_FOUND, "파일이 존재하지 않습니다.");
    }
    try {

      File file = new File(BASE_PATH + fileInfo.getPath() + "/" + fileInfo.getFileName());
      BufferedInputStream in = null;
      try {
        in = new BufferedInputStream(new FileInputStream(file));
      } catch (FileNotFoundException e) {
        log.error(e.getStackTrace().toString());
        throw new ApiException(ApiStatus.RESOURCE_NOT_FOUND, "파일이 없습니다.");
      }
      
      byte[] bytes = in.readAllBytes();
      // 파일이름 인코딩
      String header = request.getHeader("User-Agent");
      String encodedFileName;
      if ((header.contains("MSIE")) || (header.contains("Trident")) || (header.contains("Edge"))) {
        //인터넷 익스플로러 10이하 버전, 11버전, 엣지에서 인코딩
        encodedFileName = URLEncoder.encode(fileInfo.getOriginalFileName(), StandardCharsets.UTF_8);
      } else {
        //나머지 브라우저에서 인코딩
        encodedFileName = new String(fileInfo.getOriginalFileName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
      }
      // Content-Type 세팅
      MediaType contentType = findContentType(fileInfo.getExtension());

      HttpHeaders headers = new HttpHeaders();
      headers.setContentDispositionFormData("attachment", encodedFileName);
      headers.setContentType(contentType);
      headers.setContentLength(fileInfo.getSize());
      
      return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    } catch (IOException e) {
      log.error(e.getMessage());
      // 예외 처리 (파일 다운로드 실패 시)
      throw new ApiException(ApiStatus.INTERNAL_SERVER_ERROR, "파일 다운로드에 실패했습니다");
    }
  }

  @Transactional
  @Override
  public FileDto.Response save(List<MultipartFile> files, String path, String fileId) {
    FileDto.Response res = new FileDto.Response();
    FileEntity fileEntity = new FileEntity();
    List<FileDetailEntity> fileDetailEntities = new ArrayList<>();

    int fileSn = 1;

    if (fileId != null) {
      fileEntity = fileRepository.findById(fileId)
          .orElseThrow(() -> new ApiException(ApiStatus.RESOURCE_NOT_FOUND));
      Integer currentFileSn = fileDetailRepository.maxValueByFile(fileEntity);
      if (currentFileSn != null) {
        fileSn = currentFileSn + 1;
      }
    } else {
      fileEntity = fileRepository.save(fileEntity);
    }
    if (path == null) {
      path = "";
    }

    for (MultipartFile multipartFile : files) {
      String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
      String uuid = UUID.randomUUID().toString();

      String originalFileName = multipartFile.getOriginalFilename();
      String extension = getFileExtension(originalFileName);
      Long fileSize = multipartFile.getSize();
      String fileName = datetime + uuid;

      FileDetailEntity fileDetailEntity = new FileDetailEntity();
      fileDetailEntity.setFileNum(fileSn++);
      fileDetailEntity.setFile(fileEntity);
      fileDetailEntity.setFileNm(fileName);
      fileDetailEntity.setOrigFileNm(originalFileName);
      fileDetailEntity.setSz(fileSize);
      fileDetailEntity.setExt(extension);
      fileDetailEntity.setPth(path);

      fileDetailEntities.add(fileDetailEntity);

      try {
        // 스토리지에 파일 저장
        File file = new File(getFolderPath(path) + "/" + fileName);
        if (!file.createNewFile()) {
          throw new IOException();
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
          fos.write(multipartFile.getBytes());
        }
      } catch (IOException e) {
        throw new ApiException(ApiStatus.INTERNAL_SERVER_ERROR, "파일저장중 오류가 발생했습니다.");
      }
    }
    fileDetailRepository.saveAll(fileDetailEntities);

    res.setFileId(fileEntity.getFileId());
    res.setFiles(fileDetailEntities.stream()
        .map(FileDto.Base::from)
        .toList());
    return res;
  }

  @Transactional
  @Override
  public void deleteFile(String fileId, Integer fileSn) {
    FileDto.Detail fileInfo = fileMapper.getFileDetail(fileId, fileSn);
    if (fileInfo == null) {
      throw new ApiException(ApiStatus.RESOURCE_NOT_FOUND);
    }
    fileMapper.deleteFileDetail(fileInfo);
    String fileKey = fileInfo.getPath() + fileInfo.getFileName();
    File file = new File(BASE_PATH + fileInfo.getPath() + "/" + fileInfo.getFileName());

    if (!file.exists()) {
      throw new ApiException(ApiStatus.RESOURCE_NOT_FOUND, "파일이 없습니다.");
    }
    if (!file.delete()) {
      throw new ApiException(ApiStatus.INTERNAL_SERVER_ERROR, "파일 삭제하는데 오류가 발생했습니다.");
    }
  }

  private String getFolderPath(String path) {
    Date now = new Date();
//    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMM");
//    String folderPath = path + "/" + simpleDateFormat.format(now);
    String folderPath = path.startsWith("/") ? path : "/" + path;
    String fullPath = BASE_PATH + folderPath;
    File folder = new File(fullPath);
    if (!folder.exists()) {
      try {
        folder.mkdirs();
      } catch (Exception e) {
        e.getStackTrace();
        throw new ApiException(ApiStatus.INTERNAL_SERVER_ERROR);
      }
    }
    return fullPath;
  }
}
