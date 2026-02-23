package com.pug.partner.presenter.dtos;

import com.pug.identity.presenter.dtos.AccountResponse;

/**
 * Response representation of a Staff member.
 *
 * @param account the account response associated with the staff member.
 * @param entity the entityId response associated with the staff member.
 */
public record StaffResponse(AccountResponse account, EntityResponse entity) {}
