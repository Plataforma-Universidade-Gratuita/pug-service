/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.infra.persistence;

import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

/**
 * JPA entity representing Administrator privileges within the persistence layer.
 *
 * <p>This class is the database-mapped counterpart to the {@link Admin} domain aggregate. Instead
 * of a standalone ID, it uses the linked account's UUID as its primary key, effectively functioning
 * as a one-to-one extension of an {@link AccountEntity} to grant system administration rights.
 */
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

  @Enumerated(EnumType.STRING)
  @Column(name = "campus", nullable = false, length = 16)
  private Campi campus;
}
