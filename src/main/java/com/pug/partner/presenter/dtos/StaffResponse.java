package com.pug.partner.presenter.dtos;

import com.pug.identity.presenter.dtos.UserResponse;

/**
 * Response representation of a Staff member.
 *
 * @param user the user response associated with the staff member
 * @param entity the entity response associated with the staff member
 */
public record StaffResponse(UserResponse user, EntityResponse entity) {}
