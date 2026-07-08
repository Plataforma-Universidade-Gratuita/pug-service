package br.org.catolicasc.pug.shared.infra.audit;

import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.shared.utils.DiffUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.UUID;
import org.jboss.logging.MDC;

/**
 * Centralized service responsible for publishing domain-level audit events.
 *
 * <p>This component provides a clean, abstraction-heavy API for services to record state mutations.
 * By centralizing event publication, it decouples business logic from audit persistence and ensures
 * consistent event structure throughout the application.
 *
 * <p>All events are fired asynchronously to ensure that audit logging does not impact the latency
 * or success of the primary business transactions.
 */
@ApplicationScoped
public class AuditPublisher {

  @Inject Event<DomainAuditEvent> auditEvent;

  @Inject AuthService authService;

  /**
   * Publishes an asynchronous event signifying that a new entity has been created.
   *
   * @param entityName the name of the entity (e.g., "Project")
   * @param entityId the unique identifier of the newly created entity
   */
  public void fireCreate(String entityName, UUID entityId) {
    String cid =
        MDC.get("X-Correlation-Id") != null ? MDC.get("X-Correlation-Id").toString() : "N/A";
    UUID userId = authService.getCurrentAccountId();
    auditEvent.fireAsync(new DomainAuditEvent(entityName, entityId, "CREATE", null, userId, cid));
  }

  /**
   * Publishes an asynchronous event signifying that an entity has been deleted.
   *
   * @param entityName the name of the entity
   * @param entityId the unique identifier of the deleted entity
   */
  public void fireDelete(String entityName, UUID entityId) {
    String cid =
        MDC.get("X-Correlation-Id") != null ? MDC.get("X-Correlation-Id").toString() : "N/A";
    UUID userId = authService.getCurrentAccountId();
    auditEvent.fireAsync(new DomainAuditEvent(entityName, entityId, "DELETE", null, userId, cid));
  }

  /**
   * Calculates the delta between two states and publishes an update event if changes are detected.
   *
   * <p>This method performs a deep comparison between the {@code oldObj} and {@code newObj}. If
   * differences are identified, a {@link DomainAuditEvent} is dispatched with the calculated {@link
   * Map} of field changes. If no differences are found, the event is suppressed to prevent noisy
   * audit logs.
   *
   * @param entityName the name of the entity being updated
   * @param entityId the unique identifier of the entity
   * @param oldObj the original state of the entity
   * @param newObj the updated state of the entity
   */
  public void fireUpdate(String entityName, UUID entityId, Object oldObj, Object newObj) {
    Map<String, FieldChange> changes = DiffUtils.diff(oldObj, newObj);
    String cid =
        MDC.get("X-Correlation-Id") != null ? MDC.get("X-Correlation-Id").toString() : "N/A";
    UUID userId = authService.getCurrentAccountId();
    if (!changes.isEmpty()) {
      auditEvent.fireAsync(
          new DomainAuditEvent(entityName, entityId, "UPDATE", changes, userId, cid));
    }
  }
}
