package br.org.catolicasc.pug.helpers;

import br.org.catolicasc.pug.shared.infra.audit.AuditListener;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.test.Mock;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

/**
 * Mock initializer responsible for cleaning up audit logs before test execution.
 *
 * <p>This component ensures that each test run starts with a clean slate in the MongoDB audit
 * collection, preventing side effects from previous tests from polluting the audit trail.
 */
@Mock
@ApplicationScoped
public class MongoTestInitializer {

  @Inject AuditListener auditListener;

  /**
   * Observer method triggered upon application startup to purge all audit records.
   *
   * @param ev the startup event observed by this bean
   */
  public void onStart(@Observes StartupEvent ev) {
    auditListener.deleteAll();
  }
}
