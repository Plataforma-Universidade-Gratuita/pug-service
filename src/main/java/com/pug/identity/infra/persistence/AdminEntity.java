package com.pug.identity.infra.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Admin entity representing admin users in the system. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "accountId")
@ToString(of = {"accountId", "grantedAt"})
@Entity
@Table(name = "admins")
@Builder(toBuilder = true)
public class AdminEntity {

  @Id
  @Column(name = "account_id", nullable = false, updatable = false)
  private UUID accountId;

  @Column(name = "granted_at", nullable = false, updatable = false)
  private OffsetDateTime grantedAt;
}
