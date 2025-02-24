package com.example.boilerplatespringboot.api.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "file_tbl")
public class FileEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private String fileId;
  @Column
  @CreationTimestamp
  private LocalDateTime crtAt;
//  @Column
//  private LocalDateTime delAt;


  @OneToMany(mappedBy = "file",
      fetch = FetchType.EAGER,
      cascade = CascadeType.PERSIST
  )
  List<FileDetailEntity> fileDetailList;
}
