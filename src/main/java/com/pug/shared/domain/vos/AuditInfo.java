package com.pug.shared.domain.vos;

import com.pug.shared.domain.DomainError;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.time.OffsetDateTime;

/**
 * Value Object representing audit information (created and updated timestamps). Extends DomainError to allow deferred validation.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class AuditInfo extends DomainError {
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;

    @Builder(toBuilder = true)
    private AuditInfo(OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Factory method to create a new AuditInfo with the current timestamp for both createdAt and updatedAt.
     *
     * @return The AuditInfo instance (which may contain errors)
     */
    public static AuditInfo factory() {
        OffsetDateTime now = OffsetDateTime.now();
        return factory(now, now);
    }

    /**
     * Factory method to create an AuditInfo with specified timestamps. Useful for testing or when timestamps are provided externally.
     *
     * @param createdAt the creation timestamp
     * @param updatedAt the last update timestamp
     * @return The AuditInfo instance (which may contain errors)
     */
    public static AuditInfo factory(OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        AuditInfo vo = AuditInfo.builder().createdAt(createdAt).updatedAt(updatedAt).build();
        vo.collectValidationProblems();
        return vo;
    }

    /**
     * Behavior: updates the updatedAt timestamp to the current time.
     * The original createdAt is preserved automatically by toBuilder().
     *
     * @return the updated AuditInfo instance
     */
    public AuditInfo update() {
        var vo = toBuilder()
                .updatedAt(OffsetDateTime.now())
                .build();

        vo.collectValidationProblems();
        return vo;
    }

    /** Validates the period dates. */
    private void collectValidationProblems() {
        validateAuditedFields(createdAt, updatedAt);
    }
}
