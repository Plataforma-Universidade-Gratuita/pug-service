package br.org.catolicasc.pug.helpers.builders;

import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.util.Random;
import java.util.UUID;

/**
 * Builder class for creating {@link Admin} domain aggregates in test scenarios.
 *
 * <p>Provides a fluent API to define administrator properties, including campus assignment, with
 * sensible defaults for mandatory fields.
 */
public class AdminBuilder {
  private UUID accountId = UUID.randomUUID();
  private Campi campus = getRandomCampus();

  private AdminBuilder() {}

  /**
   * Initializes a new instance of the AdminBuilder.
   *
   * @return a new AdminBuilder instance
   */
  public static AdminBuilder anAdmin() {
    return new AdminBuilder();
  }

  /**
   * Helper to retrieve a random campus.
   *
   * @return a randomly selected {@link Campi}
   */
  private Campi getRandomCampus() {
    Campi[] values = Campi.values();
    return values[new Random().nextInt(values.length)];
  }

  /**
   * Sets the account ID linked to these administrative privileges.
   *
   * @param accountId the UUID of the account
   * @return this builder instance
   */
  public AdminBuilder forAccount(UUID accountId) {
    this.accountId = accountId;
    return this;
  }

  /**
   * Sets the campus jurisdiction for this administrator.
   *
   * @param campus the university {@link Campi}
   * @return this builder instance
   */
  public AdminBuilder atCampus(Campi campus) {
    this.campus = campus;
    return this;
  }

  /**
   * Constructs the {@link Admin} aggregate using the current builder state.
   *
   * @return a configured {@link Admin} instance
   */
  public Admin build() {
    return Admin.factory(accountId, campus);
  }
}
