package com.pug.partner.presenter.dtos;

import com.pug.identity.presenter.dtos.AccountResponse;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for Staff members.
 * <p>
 * This record consolidates the deeply nested backend structure across the Identity,
 * Partner, and Geo domains into a single, comprehensive response representing a user's
 * organizational privileges.
 *
 * @param account the consolidated, client-facing projection of the authentication account and user profile
 * @param entity  the consolidated, client-facing projection of the partner organization and its location
 */
public record StaffResponse(AccountResponse account, EntityResponse entity) {
}