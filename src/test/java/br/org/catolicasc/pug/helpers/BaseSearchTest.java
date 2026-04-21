package br.org.catolicasc.pug.helpers;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;
import org.hibernate.search.mapper.orm.Search;

@QuarkusTest
public abstract class BaseSearchTest {

  @Inject protected UserTransaction utx;
  @Inject protected EntityManager em;

  /**
   * Manually triggers the MassIndexer to ensure the index is in sync with the DB. Can be called
   * after any test setup that requires indexing.
   */
  protected void syncIndex(Class<?> entityClass) throws InterruptedException {
    Search.session(em).massIndexer(entityClass).startAndWait();
  }

  /** Helper to perform setup and commit so indexer can see the data. */
  protected void runInTransaction(Runnable runnable) throws Exception {
    utx.begin();
    runnable.run();
    utx.commit();
  }
}
