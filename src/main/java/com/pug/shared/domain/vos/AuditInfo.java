package com.pug.shared.domain.vos;

import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.enums.SharedFieldErrorCodes;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.time.OffsetDateTime;

/**
 * Immutable Value Object (VO) representing audit information for an entity.
 * <p>
 * Tracks the creation and last modification timestamps. It extends {@link DomainError}
 * to accumulate validation failures related to temporal integrity without throwing immediate exceptions.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class AuditInfo extends DomainError {

  /**
   * The timestamp indicating when the entity was initially created.
   */
  OffsetDateTime createdAt;

  /**
   * The timestamp indicating when the entity was last updated.
   */
  OffsetDateTime updatedAt;

  /**
   * Constructs an {@code AuditInfo} instance.
   *
   * @param createdAt the initial creation timestamp
   * @param updatedAt the last update timestamp
   */
  @Builder(toBuilder = true)
  private AuditInfo(OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  /**
   * Factory method to create a fresh {@code AuditInfo} instance for a newly created entity.
   * <p>
   * Sets both the {@code createdAt} and {@code updatedAt} timestamps to the current system time.
   * The created instance automatically collects any inherent validation problems.
   *
   * @return a new, self-validated {@link AuditInfo} instance
   */
  public static AuditInfo factory() {
    OffsetDateTime now = OffsetDateTime.now();
    return factory(now, now);
  }

  /**
   * Factory method to create an {@code AuditInfo} instance with specified timestamps.
   * <p>
   * This method is useful for reconstructing existing entities from persistence or external sources,
   * where the creation and update times are already known. The created instance automatically collects any inherent validation problems.
   *
   * @param createdAt the creation timestamp to set
   * @param updatedAt the update timestamp to set
   * @return a new, self-validated {@link AuditInfo} instance with specified timestamps
   */
  public static AuditInfo factory(OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    AuditInfo vo = AuditInfo.builder().createdAt(createdAt).updatedAt(updatedAt).build();
    vo.collectValidationProblems();
    return vo;
  }

    /**
   * Generates a new {@code AuditInfo} instance reflecting a state update.
   * <p>
   * Because Value Objects are immutable, this method leverages the builder to return a
   * copy of the current instance with the {@code updatedAt} timestamp set to the current time,
   * while preserving the original {@code createdAt} timestamp. The new instance is then validated.
   *
   * @return a new, updated, and self-validated {@link AuditInfo} instance
   */
  public AuditInfo update() {
    var vo = toBuilder().updatedAt(OffsetDateTime.now()).build();
    vo.collectValidationProblems();
    return vo;
  }

  /**
   * Evaluates internal constraints and accumulates validation problems.
   * <p>
   * Validates the following business rules:
   * <ul>
   *   <li>{@code createdAt} cannot be null (appends {@link SharedFieldErrorCodes#INVALID_CREATED_AT_BLANK})</li>
   *   <li>{@code updatedAt} cannot be null (appends {@link SharedFieldErrorCodes#INVALID_UPDATED_AT_BLANK})</li>
   *   <li>{@code updatedAt} cannot precede {@code createdAt} (appends {@link SharedFieldErrorCodes#INVALID_UPDATED_AT_BEFORE_CREATED_AT})</li>
   * </ul>
   */
  private void collectValidationProblems() {
    if (createdAt == null) {
      addFieldError(SharedFieldErrorCodes.INVALID_CREATED_AT_BLANK);
    }
    if (updatedAt == null) {
      addFieldError(SharedFieldErrorCodes.INVALID_UPDATED_AT_BLANK);
    }
    if (createdAt != null && updatedAt != null && updatedAt.isBefore(createdAt)) {
      addFieldError(SharedFieldErrorCodes.INVALID_UPDATED_AT_BEFORE_CREATED_AT);
    }
  }
}