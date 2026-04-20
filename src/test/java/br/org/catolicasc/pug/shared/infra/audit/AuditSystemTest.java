package br.org.catolicasc.pug.shared.infra.audit;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@QuarkusTest
class AuditSystemTest {

    @Inject
    AuditPublisher auditPublisher;
    @Inject
    AuditListener auditListener;

    @Test
    @DisplayName("AuditPublisher should fire event and AuditListener should persist it")
    void testAuditPersistence() {
        UUID entityId = UUID.randomUUID();

        auditPublisher.fireCreate("TestEntity", entityId);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            long count = auditListener.count("entityId", entityId);
            assertThat(count).isEqualTo(1);
        });
    }
}