package com.example.boilerplatespringboot.api.common.service;

import com.example.boilerplatespringboot.api.common.dto.FileDto;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@AllArgsConstructor
public class EncryptService {

  private final AesBytesEncryptor encryptor;

  public String encryptString(String str) {
    byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
    byte[] encrypt = encryptor.encrypt(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypt);
  }

  public String decryptString(String encryptedStr) {
    byte[] decodedBytes = Base64.getUrlDecoder().decode(encryptedStr);
    byte[] decryptedBytes = encryptor.decrypt(decodedBytes);
    return new String(decryptedBytes, StandardCharsets.UTF_8);
  }

  public FileDto.Base encryptFileBase(FileDto.Base prev) {
    FileDto.Base next = new FileDto.Base();

    String fileId = prev.getFileId();
    Integer fileSn = prev.getFileSn();
    String encryptedFileId = encryptString(fileId);

    next.setFileId(encryptedFileId);
    next.setFileSn(fileSn);

    return next;
  }

  public List<FileDto.Base> encryptFileBaseList(List<FileDto.Base> prev) {
    if (prev != null) {
      return prev.stream().map(this::encryptFileBase).toList();
    }
    return new ArrayList<>();
  }
}
