package br.org.catolicasc.pug.shared.infra.audit;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.util.Collections;
import java.util.List;
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

  /** A list of field-level changes capturing old and new values. */
  private List<FieldChange> changes;

  /** The unique identifier (Account ID) of the user who performed the change. */
  private UUID performedBy;

  /** ISO-8601 formatted timestamp string. */
  private String timestamp;

  /**
   * The correlation ID extracted from the request, used to trace the audit log to specific API
   * requests.
   */
  private String correlationId;

  public List<FieldChange> getChanges() {
    return changes != null ? Collections.unmodifiableList(changes) : null;
  }

  /** Custom builder method to ensure immutability of the changes list. */
  public static class AuditLogBuilder {
    public AuditLogBuilder changes(List<FieldChange> changes) {
      this.changes = (changes != null) ? new java.util.ArrayList<>(changes) : null;
      return this;
    }
  }
}
