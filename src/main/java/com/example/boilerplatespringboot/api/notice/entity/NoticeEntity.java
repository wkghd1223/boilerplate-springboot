package com.example.boilerplatespringboot.api.notice.entity;


import jakarta.persistence.*;
import com.example.boilerplatespringboot.api.auth.entity.UserEntity;
import com.example.boilerplatespringboot.api.notice.dto.NoticeDto;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "ntc_tbl")
@SQLDelete(sql = "update service.ntc_tbl set del_at = current_timestamp where ntc_id = ?")
@SQLRestriction("del_at is NULL")
public class NoticeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long ntcId;
  @Column
  private String titl;
  @Column
  private String cntnt;
  @CreationTimestamp
  @Column
  private LocalDateTime crtAt;
  @Column
  private LocalDateTime delAt;

  @ManyToOne
  @JoinColumn(name = "athr_id")
  private UserEntity user;

  public static NoticeEntity from(NoticeDto.Create dto) {
    NoticeEntity entity = new NoticeEntity();
    entity.setTitl(dto.getTitle());
    entity.setCntnt(dto.getContent());
    UserEntity userEntity = new UserEntity();
    userEntity.setUsrId(dto.getAuthorId());
    entity.setUser(userEntity);
    return entity;
  }
}
