/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.shared.infra.audit;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jboss.logging.Logger;

/**
 * Asynchronous listener that persists audit events to MongoDB.
 *
 * <p>This listener observes {@link DomainAuditEvent} events fired throughout the application and
 * persists them into the configured MongoDB collection. It runs asynchronously, ensuring that audit
 * logging does not impose latency on primary business transactions.
 */
@ApplicationScoped
public class AuditListener implements PanacheMongoRepository<AuditLog> {

  private static final Logger LOG = Logger.getLogger(AuditListener.class);

  /**
   * Consumes domain events and writes them to the audit database.
   *
   * @param event the audit event payload to be persisted
   */
  public void onAuditEvent(@ObservesAsync DomainAuditEvent event) {
    try {
      AuditLog log =
          AuditLog.builder()
              .entityName(event.entityName())
              .entityId(event.entityId())
              .action(event.action())
              .changes(toChangeList(event.changes()))
              .performedBy(event.performedBy())
              .timestamp(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
              .correlationId(event.correlationId())
              .build();

      persist(log);
      LOG.debugf("Audit log persisted for entity: %s", event.entityName());
    } catch (Exception e) {
      // Catch exception to prevent the async observer from failing the entire process
      LOG.errorf(e, "Failed to persist audit log for entity: %s", event.entityName());
    }
  }

  private static List<FieldChange> toChangeList(Map<String, FieldChange> src) {
    if (src == null) {
      return null;
    }
    return new ArrayList<>(src.values());
  }
}
