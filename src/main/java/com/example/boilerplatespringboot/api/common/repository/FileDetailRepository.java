package com.example.boilerplatespringboot.api.common.repository;

import io.swagger.v3.oas.annotations.Hidden;
import com.example.boilerplatespringboot.api.common.entity.FileDetailEntity;
import com.example.boilerplatespringboot.api.common.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Hidden
public interface FileDetailRepository extends JpaRepository<FileDetailEntity, FileDetailEntity.FileDetailEntityId> {
  @Query("select max(fd.fileNum) from FileDetailEntity fd where fd.file = ?1")
  Integer maxValueByFile(FileEntity file);
}