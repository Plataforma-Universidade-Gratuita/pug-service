package com.pug.shared.infra.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

/**
 * Abstract base class for JPA entities requiring standard temporal auditing.
 * <p>
 * Extends {@link BaseUuidV7Entity} to inherit the primary key, while automatically
 * incorporating consistent creation and modification timestamps. Using {@link OffsetDateTime}
 * ensures that all audit records are stored cleanly with timezone awareness.
 */
@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseAuditedEntity extends BaseUuidV7Entity {

  /**
   * The exact timestamp when the entity was first persisted to the database.
   * <p>
   * This column is strictly immutable post-creation ({@code updatable = false})
   * to preserve accurate historical auditing.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  /**
   * The timestamp indicating when the entity's state was last updated.
   * <p>
   * This value should be refreshed via application logic or JPA lifecycle callbacks
   * whenever the entity is modified.
   */
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;
}