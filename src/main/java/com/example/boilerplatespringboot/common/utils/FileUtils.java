package com.example.boilerplatespringboot.common.utils;

import org.springframework.http.MediaType;

import java.util.Arrays;

public class FileUtils {

  public static String getFileExtension(String fileName) {
    if (fileName == null || fileName.lastIndexOf(".") == -1 || fileName.lastIndexOf(".") == fileName.length() - 1) {
      return ""; // Return empty string if there's no extension
    }
    return fileName.substring(fileName.lastIndexOf(".") + 1);
  }

  public static MediaType findContentType(String extension) {
    MediaType contentType;
    final String[] IMAGE_TYPES = {"jpg", "jpeg", "png"};
    final String PDF_TYPE = "pdf";
    if (PDF_TYPE.equals(extension)) {
      contentType = MediaType.APPLICATION_PDF;
    } else if (Arrays.stream(IMAGE_TYPES).toList().contains(extension)) {
      contentType = MediaType.IMAGE_PNG;
    } else {
      contentType = MediaType.APPLICATION_OCTET_STREAM;
    }
    return contentType;
  }
}
