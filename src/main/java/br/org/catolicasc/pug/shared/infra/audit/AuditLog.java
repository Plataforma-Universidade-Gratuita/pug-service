package br.org.catolicasc.pug.shared.infra.audit;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.util.ArrayList;
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

  @BsonId private String id;

  private String entityName;

  private UUID entityId;

  private String action;

  private List<FieldChange> changes;

  private UUID performedBy;

  private String timestamp;

  private String correlationId;

  /** Custom getter to return an unmodifiable view of the changes list, ensuring immutability. */
  public List<FieldChange> getChanges() {
    return changes != null ? Collections.unmodifiableList(changes) : null;
  }

  /** Custom builder method to ensure immutability of the changes list. */
  public static class AuditLogBuilder {
    public AuditLogBuilder changes(List<FieldChange> changes) {
      this.changes = (changes != null) ? new ArrayList<>(changes) : null;
      return this;
    }
  }
}
