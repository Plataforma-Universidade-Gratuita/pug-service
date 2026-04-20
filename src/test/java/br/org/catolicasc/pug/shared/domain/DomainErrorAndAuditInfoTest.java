package br.org.catolicasc.pug.shared.domain;

import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.vos.AuditInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Domain Logic Tests")
class DomainErrorAndAuditInfoTest {

    static class TestEntity extends DomainError {
        void validateId(UUID id) {
            validateIdField(id);
        }

        void validateName(String name) {
            validateNameField(name);
        }
    }

    @Nested
    @DisplayName("Class: DomainError")
    class DomainErrorTests {
        @Test
        @DisplayName("Should accumulate multiple validation errors")
        void shouldAccumulateErrors() {
            TestEntity entity = new TestEntity();
            entity.validateId(null);
            entity.validateName("");

            assertThat(entity.hasFieldErrors()).isTrue();
            assertThat(entity.getFieldErrors()).contains(
                    SharedFieldErrorCodes.INVALID_ID_BLANK,
                    SharedFieldErrorCodes.INVALID_NAME_BLANK
            );
            assertThat(entity.getProblemsSummary()).contains("id", "name");
        }
    }

    @Nested
    @DisplayName("Class: AuditInfo")
    class AuditInfoTests {
        @Test
        @DisplayName("Should create valid AuditInfo with current time")
        void shouldCreateValid() {
            AuditInfo audit = AuditInfo.factory();
            assertThat(audit.hasFieldErrors()).isFalse();
            assertThat(audit.getCreatedAt()).isBeforeOrEqualTo(audit.getUpdatedAt());
        }

        @Test
        @DisplayName("Should detect error when updatedAt precedes createdAt")
        void shouldDetectTemporalError() {
            OffsetDateTime now = OffsetDateTime.now();
            AuditInfo audit = AuditInfo.factory(now, now.minusDays(1));

            assertThat(audit.hasFieldErrors()).isTrue();
            assertThat(audit.getFieldErrors()).contains(SharedFieldErrorCodes.INVALID_UPDATED_AT_BEFORE_CREATED_AT);
        }

        @Test
        @DisplayName("Should update updatedAt on call")
        void shouldUpdateTimestamp() throws InterruptedException {
            AuditInfo original = AuditInfo.factory();
            Thread.sleep(10);
            AuditInfo updated = original.update();

            assertThat(updated.getUpdatedAt()).isAfter(original.getUpdatedAt());
            assertThat(updated.getCreatedAt()).isEqualTo(original.getCreatedAt());
        }
    }
}