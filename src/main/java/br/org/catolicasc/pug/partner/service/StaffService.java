package br.org.catolicasc.pug.partner.service;

import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.partner.service.dtos.staff.StaffCreateCommand;
import br.org.catolicasc.pug.partner.service.dtos.staff.StaffUpdateCommand;
import java.util.UUID;

/**
 * Application service interface for managing the state of {@link Staff} domain aggregates.
 *
 * <p>This command-side service exposes operations to create, retrieve, update, activate/deactivate,
 * and delete partner staff members. Implementations coordinate staff persistence with linked
 * account management, partner entity validation, business-rule enforcement, and audit publication.
 */
public interface StaffService {

  /**
   * Permanently deletes a staff member by its linked account identifier.
   *
   * <p>Implementations must prevent deletion when the staff member is still linked to protected
   * business records, such as created projects or validated attendances. When deletion succeeds,
   * the linked account is also removed.
   *
   * @param accountId the unique identifier of the account linked to the staff member
   * @return {@code true} when the staff member was deleted, or {@code false} when the identifier is
   *     null or no matching staff member exists
   */
  boolean delete(UUID accountId);

  /**
   * Permanently deletes all staff members linked to a partner entity.
   *
   * <p>When staff members are found for the given entity, their linked accounts are also removed.
   *
   * @param entityId the unique identifier of the partner entity whose staff members should be
   *     deleted
   * @return the number of staff members deleted, or {@code 0} when the identifier is null or no
   *     staff members are linked to the entity
   */
  long deleteAllByEntityId(UUID entityId);

  /**
   * Retrieves a staff aggregate by its linked account identifier.
   *
   * @param accountId the unique identifier of the account linked to the staff member
   * @return the matching {@link Staff} aggregate
   */
  Staff getByAccountId(UUID accountId);

  /**
   * Registers a new partner staff member.
   *
   * <p>Implementations must validate the target partner entity, create the linked account, and
   * ensure the account is not already registered as staff for the same or another partner entity.
   *
   * @param cmd the command containing the linked account data and target partner entity identifier
   * @return the persisted {@link Staff} aggregate
   */
  Staff save(StaffCreateCommand cmd);

  /**
   * Updates an existing staff member, including optional transfer to a different partner entity.
   *
   * <p>Implementations may update the linked account data and, when a new entity is provided,
   * validate the target entity and ensure the effective email is not already used by another staff
   * member within that entity.
   *
   * @param accountId the unique identifier of the account linked to the staff member to update
   * @param cmd the command containing the modified staff and linked account data
   * @return the updated {@link Staff} aggregate
   */
  Staff update(UUID accountId, StaffUpdateCommand cmd);

  /**
   * Updates the activation status of the linked staff account.
   *
   * @param accountId the unique identifier of the account linked to the staff member
   * @param active whether the linked account should be active
   * @return the updated {@link Staff} aggregate
   */
  Staff updateStatus(UUID accountId, boolean active);
}
