package com.example.boilerplatespringboot.api.common.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "file_dtl_tbl")
@IdClass(FileDetailEntity.FileDetailEntityId.class)
public class FileDetailEntity {
  @Id
  @Column
  private Integer fileNum;

  @Id
  @ManyToOne
  @JoinColumn(name = "file_id")
  private FileEntity file;

  @Column
  private String pth;
  @Column
  private String fileNm;
  @Column
  private String origFileNm;
  @Column
  private String ext;
  @Column
  private Long sz;

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @EqualsAndHashCode
  public static class FileDetailEntityId implements Serializable {
    public FileEntity file;
    public Integer fileNum;
  }
}
