package br.org.catolicasc.pug.partner.service;

import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.service.AccountService;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.partner.service.dtos.StaffCreateCommand;
import br.org.catolicasc.pug.partner.service.dtos.StaffUpdateCommand;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import java.util.UUID;

/**
 * Application service interface for managing the state of {@link Staff} domain aggregates.
 *
 * <p>Following CQRS principles, this service handles the "Command" operations (Create, Delete). It
 * orchestrates the lifecycle relationship between a Staff member, their underlying {@link Account},
 * and the {@link Entity} they represent, ensuring that access rights cascade correctly across
 * domains.
 */
public interface StaffService {

  /**
   * Deactivates a Staff member's account.
   *
   * <p>This operation delegates to the underlying {@link AccountService} to gracefully disable
   * Login.bru and operational capabilities without permanently destroying the account data,
   * preserving referential integrity for historical records.
   *
   * @param accountId the unique identifier of the Staff to deactivate (Account ID)
   * @return {@code true} if the deactivation was successful, {@code false} if it was not found
   */
  boolean deactivate(UUID accountId);

  /**
   * Revokes staff privileges by deleting the {@link Staff} record.
   *
   * <p>This operation enforces data hygiene. After the staff privileges are successfully revoked,
   * the service automatically triggers the deletion of the underlying {@link Account} to ensure
   * credentials tied strictly to organizational roles are wiped out.
   *
   * @param accountId the unique identifier of the Staff to delete (Account ID)
   * @return {@code true} if the staff record was successfully deleted, {@code false} if it was not
   *     found
   */
  boolean delete(UUID accountId);

  /**
   * Removes all {@link Staff} members associated with a specific partner organization.
   *
   * <p>This batch operation is typically invoked when a partner entity is deleted from the system.
   * It extracts all associated staff accounts and cascades the deletion down to the Identity
   * domain.
   *
   * @param entityId the unique identifier of the partner entity whose staff members should be
   *     removed
   * @return the total number of staff members that were successfully deleted
   */
  long deleteAllByEntityId(UUID entityId);

  /**
   * Retrieves a full {@link Staff} domain aggregate by its linked account identifier.
   *
   * <p><b>Note:</b> This method is intended strictly for internal domain orchestration. For API
   * responses, use {@link StaffReadService#getViewByAccountId(UUID)} instead.
   *
   * @param accountId the unique identifier (UUID) of the linked account
   * @return the fully reconstituted {@link Staff} aggregate
   * @throws ResourceNotFoundException if the Staff record does not exist
   * @throws AppValidationException if the Staff exists but its stored state violates strict domain
   *     invariants (data corruption)
   */
  Staff getByAccountId(UUID accountId);

  /**
   * Instantiates and persists a new {@link Staff} aggregate based on the provided command.
   *
   * <p>This method performs a cascading save. It verifies that the specified partner entity exists,
   * then delegates the creation of the underlying authentication account (and potentially the user)
   * to the {@link AccountService} before appending the staff privileges.
   *
   * <p><b>Business Rule:</b> An authentication account may only be assigned as Staff to a single
   * partner organization at any given time.
   *
   * @param cmd the structured command containing the data to create the staff member and linked
   *     account
   * @return the fully instantiated and persisted {@link Staff} aggregate
   * @throws DuplicateResourceException if the account email already exists, or if the account is
   *     already assigned to this exact entity
   * @throws BusinessRuleException if the account is already assigned as Staff to a different entity
   * @throws ResourceNotFoundException if the specified partner entity does not exist
   * @throws AppValidationException if input validation fails
   */
  Staff save(StaffCreateCommand cmd);

  /**
   * Partially updates a Staff member's personal information and authentication credentials.
   *
   * <p>Because a Staff member's organizational role is structurally bound to their original account
   * and partner entity, the core {@link Staff} aggregate properties ({@code accountId} and {@code
   * entityId}) are entirely immutable. This method safely delegates the mutation of the mutable
   * fields (such as name, email, or password) directly down to the Identity bounded context.
   *
   * @param accountId the unique identifier (Account ID) of the Staff member to update
   * @param cmd the structured command containing the modified account and user data
   * @return the structurally unmodified {@link Staff} aggregate, returned for operational
   *     continuity
   * @throws ResourceNotFoundException if the Staff record does not exist
   * @throws DuplicateResourceException if the updated email conflicts with an existing account in
   *     the system
   * @throws AppValidationException if the provided update data violates domain constraints
   */
  Staff update(UUID accountId, StaffUpdateCommand cmd);
}
