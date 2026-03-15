package com.pug.shared.infra.search;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.search.mapper.orm.Search;
import org.jboss.logging.Logger;

/**
 * Application-scoped component responsible for initializing the Hibernate Search mass indexing
 * process.
 *
 * <p>This class listens for the Quarkus {@link StartupEvent} and triggers the {@code MassIndexer}.
 * This synchronization is critical because data inserted directly via SQL scripts (such as Flyway
 * migrations for cities or the initial system admin) bypasses the standard JPA entity lifecycle.
 * Without this manual synchronization step, Hibernate Search would not automatically forward those
 * records to the Elasticsearch cluster, resulting in empty search results.
 */
@ApplicationScoped
public class SearchIndexer {

  private static final Logger LOG = Logger.getLogger(SearchIndexer.class);

  @Inject EntityManager em;

  /**
   * Executes the mass indexing operation upon application startup.
   *
   * <p>It instructs the underlying {@link org.hibernate.search.mapper.orm.session.SearchSession} to
   * completely rebuild the Elasticsearch indexes based on the current state of the relational
   * database. The process is awaited (blocked) to ensure the indexes are fully populated before the
   * application begins accepting complex full-text search queries.
   *
   * @param ev the Quarkus {@link StartupEvent} payload indicating the application has successfully
   *     started
   */
  @Transactional
  public void indexOnStart(@Observes StartupEvent ev) {
    LOG.info("Starting Hibernate Search MassIndexer to sync DB with Elasticsearch...");
    try {
      Search.session(em).massIndexer().startAndWait();
      LOG.info("MassIndexer completed successfully!");
    } catch (InterruptedException e) {
      LOG.error("MassIndexer was interrupted", e);
      Thread.currentThread().interrupt();
    }
  }
}
