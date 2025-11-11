package com.pug.identity.infra.persistence;

import com.pug.shared.infra.persistence.TimestampColumnsListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Admins entity representing admin users in the system. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "accountId")
@ToString(of = {"accountId"})
@Entity
@Table(name = "admins")
@EntityListeners(TimestampColumnsListener.class)
public class AdminEntity {

  @Id
  @Column(name = "account_id", nullable = false, updatable = false)
  private UUID accountId;

  @Column(name = "granted_at", nullable = false, updatable = false)
  private OffsetDateTime grantedAt;
}
