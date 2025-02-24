package com.example.boilerplatespringboot.api.notice.repository;

import io.swagger.v3.oas.annotations.Hidden;
import com.example.boilerplatespringboot.api.notice.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

@Hidden
public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {
}
