package com.pug.identity.infra.persistence;

import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.infra.persistence.BaseAuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * Entity representing a user in the system.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(
        callSuper = true,
        of = {"email", "userId"})
@Entity
@Table(
        name = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_accounts_email",
                        columnNames = {"email"})
        },
        indexes = {
                @Index(name = "idx_accounts_email", columnList = "email"),
        })
@SuperBuilder
public class AccountEntity extends BaseAuditedEntity {

  @Column(name = "user_id", nullable = false, updatable = false)
  private UUID userId;

  @Column(name = "email", nullable = false, length = 254)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(name = "account_type", nullable = false, length = 16)
  private AccountType accountType;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;
}
