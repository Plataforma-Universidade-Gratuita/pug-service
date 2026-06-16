package br.org.catolicasc.pug.shared.infra.audit;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/** Event payload for triggering an asynchronous audit log entry. */
public record DomainAuditEvent(
    String entityName,
    UUID entityId,
    String action,
    Map<String, FieldChange> changes,
    UUID performedBy,
    String correlationId) {

  /**
   * Constructs a new DomainAuditEvent, ensuring that the changes map is immutable if provided.
   *
   * @param entityName the name of the audited entity (e.g., "Account", "Course")
   * @param entityId the unique identifier of the audited entity
   * @param action the type of action performed (e.g., "CREATE", "UPDATE", "DELETE")
   * @param changes a map detailing the field changes, where keys are field names and values are
   *     FieldChange objects
   * @param performedBy the unique identifier of the user who performed the action
   * @param correlationId a unique identifier for correlating this audit event with related
   *     operations or logs
   */
  public DomainAuditEvent {
    changes = (changes != null) ? Map.copyOf(changes) : null;
  }

  /**
   * Retrieves an unmodifiable view of the changes map, or null if no changes were provided.
   *
   * @return an unmodifiable map of field changes, or null if no changes exist
   */
  public Map<String, FieldChange> changes() {
    return changes != null ? Collections.unmodifiableMap(changes) : null;
  }
}
