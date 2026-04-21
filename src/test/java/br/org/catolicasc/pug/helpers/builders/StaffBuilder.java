package br.org.catolicasc.pug.helpers.builders;

import br.org.catolicasc.pug.partner.domain.Staff;
import java.util.UUID;

/**
 * Builder class for creating {@link Staff} domain aggregates in test scenarios.
 *
 * <p>Provides a fluent API to define staff properties, linking an authentication account to a
 * partner entity.
 */
public class StaffBuilder {
  private UUID accountId = UUID.randomUUID();
  private UUID entityId = UUID.randomUUID();

  private StaffBuilder() {}

  /**
   * Initializes a new instance of the StaffBuilder.
   *
   * @return a new StaffBuilder instance
   */
  public static StaffBuilder aStaff() {
    return new StaffBuilder();
  }

  /**
   * Sets the account identifier linked to this staff assignment.
   *
   * @param id the UUID of the account
   * @return this builder instance
   */
  public StaffBuilder forAccount(UUID id) {
    this.accountId = id;
    return this;
  }

  /**
   * Sets the entity identifier linked to this staff assignment.
   *
   * @param id the UUID of the partner entity
   * @return this builder instance
   */
  public StaffBuilder forEntity(UUID id) {
    this.entityId = id;
    return this;
  }

  /**
   * Constructs the {@link Staff} aggregate using the current builder state.
   *
   * @return a configured {@link Staff} instance
   */
  public Staff build() {
    return Staff.factory(accountId, entityId);
  }
}
