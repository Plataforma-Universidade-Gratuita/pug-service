package br.org.catolicasc.pug.shared.infra.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AuditSystemTest {

  @Inject AuditPublisher auditPublisher;
  @Inject AuditListener auditListener;

  @AfterEach
  void cleanup() {
    auditListener.deleteAll();
  }

  @Test
  @DisplayName("AuditPublisher should fire event and AuditListener should persist it")
  void testAuditPersistence() {
    UUID entityId = UuidCreator.getTimeOrderedEpoch();

    auditPublisher.fireCreate("TestEntity", entityId);

    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(
            () -> {
              long count = auditListener.count("entityId", entityId);
              assertThat(count).isEqualTo(1);
            });
  }
}
