package com.example.boilerplatespringboot.api.auth.repository;

import io.swagger.v3.oas.annotations.Hidden;
import com.example.boilerplatespringboot.api.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Hidden
public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByUsrnm(String username);
}
