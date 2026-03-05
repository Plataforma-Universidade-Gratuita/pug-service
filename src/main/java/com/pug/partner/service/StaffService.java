package com.pug.partner.service;

import com.pug.partner.domain.Staff;
import com.pug.partner.service.dtos.StaffCreateCommand;
import java.util.UUID;

/**
 * Application service interface for managing the state of {@link Staff} domain aggregates.
 *
 * <p>Following CQRS principles, this service handles the "Command" operations (Create, Delete). It
 * orchestrates the lifecycle relationship between a Staff member, their underlying {@link
 * com.pug.identity.domain.Account}, and the {@link com.pug.partner.domain.Entity} they represent,
 * ensuring that access rights cascade correctly across domains.
 */
public interface StaffService {

  /**
   * Instantiates and persists a new {@link Staff} aggregate based on the provided command.
   *
   * <p>This method performs a cascading save. It verifies that the specified partner entity exists,
   * then delegates the creation of the underlying authentication account (and potentially the user)
   * to the {@link com.pug.identity.service.AccountService} before appending the staff privileges.
   *
   * <p><b>Business Rule:</b> An authentication account may only be assigned as Staff to a single
   * partner organization at any given time.
   *
   * @param cmd the structured command containing the data to create the staff member and linked
   *     account
   * @return the fully instantiated and persisted {@link Staff} aggregate
   * @throws com.pug.shared.exceptions.DuplicateResourceException if the account email already
   *     exists, or if the account is already assigned to this exact entity
   * @throws com.pug.shared.exceptions.BusinessRuleException if the account is already assigned as
   *     Staff to a different entity
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the specified partner entity
   *     does not exist
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails
   */
  Staff save(StaffCreateCommand cmd);

  /**
   * Revokes staff privileges by deleting the {@link Staff} record.
   *
   * <p>This operation enforces data hygiene. After the staff privileges are successfully revoked,
   * the service automatically triggers the deletion of the underlying {@link
   * com.pug.identity.domain.Account} to ensure credentials tied strictly to organizational roles
   * are wiped out.
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
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the Staff record does not exist
   * @throws com.pug.shared.exceptions.AppValidationException if the Staff exists but its stored
   *     state violates strict domain invariants (data corruption)
   */
  Staff getByAccountId(UUID accountId);
}
