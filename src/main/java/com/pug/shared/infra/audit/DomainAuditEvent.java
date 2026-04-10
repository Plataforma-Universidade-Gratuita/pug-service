package com.pug.shared.infra.audit;

import java.util.Map;
import java.util.UUID;

/**
 * Event payload for triggering an asynchronous audit log entry.
 *
 * <p>This record is used to propagate change information from the application services to the
 * asynchronous audit listener, allowing decoupled recording of modifications without impacting the
 * primary business transaction.
 *
 * @param entityName the name of the domain entity being modified
 * @param entityId the unique identifier of the modified entity
 * @param action the type of action performed (e.g., "UPDATE")
 * @param changes a {@link Map} representing the differences or state captured during the operation
 * @param performedBy the user who triggered that transaction
 * @param correlationId the correlation identifier for distributed systems
 */
public record DomainAuditEvent(
    String entityName,
    UUID entityId,
    String action,
    Map<String, Object> changes,
    UUID performedBy,
    String correlationId) {}
