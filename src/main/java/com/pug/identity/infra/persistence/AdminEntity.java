package com.pug.identity.infra.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
public class AdminEntity {
  @Id
  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "granted_at", nullable = false, updatable = false, insertable = false)
  private Instant grantedAt;

  public AdminEntity(UUID userId) {
    this.userId = userId;
  }
}
