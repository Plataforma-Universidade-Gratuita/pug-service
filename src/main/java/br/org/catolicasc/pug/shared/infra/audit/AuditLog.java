package br.org.catolicasc.pug.shared.infra.audit;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonId;

/**
 * MongoDB document representing a system-wide audit entry.
 *
 * <p>This entity captures historical state changes across the system, linking modifications to the
 * specific user account and request correlation ID for auditability and distributed tracing.
 */
@Getter
@Builder(toBuilder = true, builderClassName = "AuditLogBuilder")
@MongoEntity(collection = "audit_logs")
public class AuditLog {

  /** The unique MongoDB identifier for the audit log entry. */
  @BsonId private String id;

  /** The name of the domain entity that was modified (e.g., "Project"). */
  private String entityName;

  /** The unique identifier of the specific entity instance that was changed. */
  private UUID entityId;

  /** The type of operation performed (e.g., "CREATE", "UPDATE", "DELETE"). */
  private String action;

  /**
   * A map capturing the structural changes, typically mapping field names to their old and new
   * values.
   */
  private Map<String, Object> changes;

  /** The unique identifier (Account ID) of the user who performed the change. */
  private UUID performedBy;

  /** The exact timestamp when the audit log entry was created. */
  private OffsetDateTime timestamp;

  /**
   * The correlation ID extracted from the request, used to trace the audit log to specific API
   * requests.
   */
  private String correlationId;

  public Map<String, Object> getChanges() {
    return changes != null ? Collections.unmodifiableMap(changes) : null;
  }

  /** Custom builder method to ensure immutability of the changes map. */
  public static class AuditLogBuilder {
    public AuditLogBuilder changes(Map<String, Object> changes) {
      this.changes = (changes != null) ? new java.util.HashMap<>(changes) : null;
      return this;
    }
  }
}
