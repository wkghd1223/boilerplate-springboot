package com.example.boilerplatespringboot.api.common.repository;

import io.swagger.v3.oas.annotations.Hidden;
import com.example.boilerplatespringboot.api.common.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;


@Hidden
public interface FileRepository extends JpaRepository<FileEntity, String> {
}