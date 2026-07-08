package br.org.catolicasc.pug.partner.presenter.dtos.staff;

import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountResponse;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for Staff members.
 *
 * <p>This record consolidates the staff member's authentication account with the identifiers of the
 * partner organization and its city. Instead of nesting the full partner or city structures, it
 * exposes {@code entityId} and {@code cityId} so that additional details can be fetched on demand
 * via dedicated endpoints.
 *
 * @param account the consolidated, client-facing projection of the authentication account and user
 *     profile
 * @param entityId the unique identifier (UUIDv7) of the partner organization the staff belongs to
 * @param cityId the unique identifier (UUIDv7) of the city where the partner organization is
 *     located
 */
public record StaffResponse(AccountResponse account, UUID entityId, UUID cityId) {}
