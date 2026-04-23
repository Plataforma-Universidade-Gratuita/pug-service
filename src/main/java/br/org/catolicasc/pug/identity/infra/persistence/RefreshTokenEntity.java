package br.org.catolicasc.pug.identity.infra.persistence;

import br.org.catolicasc.pug.shared.infra.persistence.BaseAuditedEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * JPA entity representing a refresh token used for session management.
 *
 * <p>Each record links a hashed opaque refresh token to a specific {@link AccountEntity}. The token
 * hash is stored instead of the raw token to prevent exposure in the event of a database
 * compromise.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(
    callSuper = true,
    of = {"tokenHash", "expiresAt"})
@Entity
@Table(
    name = "refresh_tokens",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_refresh_tokens_token_hash",
          columnNames = {"token_hash"})
    },
    indexes = {
      @Index(name = "idx_refresh_tokens_token_hash", columnList = "token_hash"),
      @Index(name = "idx_refresh_tokens_account_id", columnList = "account_id"),
    })
@SuperBuilder
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class RefreshTokenEntity extends BaseAuditedEntity {

  /** The account that owns this refresh token. */
  @ManyToOne(optional = false)
  @JoinColumn(name = "account_id", nullable = false, updatable = false)
  private AccountEntity account;

  /** SHA-256 hex digest of the opaque refresh token string sent to the client. */
  @Column(name = "token_hash", nullable = false, length = 128, updatable = false)
  private String tokenHash;

  /** The point in time after which this refresh token is no longer valid. */
  @Column(name = "expires_at", nullable = false, updatable = false)
  private OffsetDateTime expiresAt;
}
