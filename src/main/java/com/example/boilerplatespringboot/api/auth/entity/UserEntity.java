package com.example.boilerplatespringboot.api.auth.entity;

import jakarta.persistence.*;
import com.example.boilerplatespringboot.common.enums.Role;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "usr_tbl")
@SQLDelete(sql = "update service.usr_tbl set del_at = current_timestamp where usr_id = ?")
@SQLRestriction("del_at is NULL")
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long usrId;
  @Column(unique = true)
  private String usrnm;
  @Column
  private String pwd;
  @Column
  private String nm;
  @Column
  @Enumerated(EnumType.STRING)
  private Role role;
  @CreationTimestamp
  @Column
  private LocalDateTime crtAt;
  @Column
  private LocalDateTime delAt;

}
