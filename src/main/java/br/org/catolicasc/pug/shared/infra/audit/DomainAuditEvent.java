package br.org.catolicasc.pug.shared.infra.audit;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * Event payload for triggering an asynchronous audit log entry.
 */
public record DomainAuditEvent(
    String entityName,
    UUID entityId,
    String action,
    Map<String, FieldChange> changes,
    UUID performedBy,
    String correlationId) {

  public DomainAuditEvent {
    changes = (changes != null) ? Map.copyOf(changes) : null;
  }

  public Map<String, FieldChange> changes() {
    return changes != null ? Collections.unmodifiableMap(changes) : null;
  }
}
