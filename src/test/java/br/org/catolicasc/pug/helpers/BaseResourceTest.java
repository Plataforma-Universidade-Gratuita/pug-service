package br.org.catolicasc.pug.helpers;

import static io.restassured.RestAssured.given;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

/**
 * Base class for REST resource integration tests. Provides common injected dependencies and a
 * transaction helper that eliminates the repetitive {@code utx.begin() / em.flush() / utx.commit()}
 * boilerplate.
 */
@QuarkusTest
public abstract class BaseResourceTest {

  @Inject protected TestDataFactory factory;
  @Inject protected UserTransaction utx;
  @Inject protected EntityManager em;

  /**
   * Executes the given setup logic inside a managed transaction, flushing and committing
   * automatically. Replaces the repetitive pattern:
   *
   * <pre>{@code
   * utx.begin();
   * // ... setup ...
   * em.flush();
   * utx.commit();
   * }</pre>
   *
   * @param setup the setup logic to run transactionally
   */
  protected void doInTransaction(ThrowingRunnable setup) throws Exception {
    utx.begin();
    setup.run();
    em.flush();
    utx.commit();
  }

  /** Asserts that an unauthenticated GET request to the given path returns 401. */
  protected static void assertUnauthenticated(String path) {
    given().when().get(path).then().statusCode(401);
  }

  /** A {@link Runnable}-like interface that allows checked exceptions. */
  @FunctionalInterface
  public interface ThrowingRunnable {
    void run() throws Exception;
  }
}
