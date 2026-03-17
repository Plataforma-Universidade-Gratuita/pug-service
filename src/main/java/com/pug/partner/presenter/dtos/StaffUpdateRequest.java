package com.pug.partner.presenter.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for partially updating a Staff
 * member's underlying account and user details.
 *
 * <p>Because updates can be partial, all fields in this record are inherently optional. If a field
 * is provided as {@code null} or omitted from the JSON payload, the application service will ignore
 * it and retain the existing value for that specific attribute. The structural links (Account ID
 * and Entity ID) cannot be changed via this payload.
 *
 * @param name the new name of the staff member, or {@code null} to leave unchanged (if provided,
 *     max 100 characters)
 * @param emailString the new email address, or {@code null} to leave unchanged (if provided, must
 *     be a valid email up to 100 characters)
 * @param password the new raw password, or {@code null} to leave unchanged (if provided, must be
 *     between 8 and 100 characters)
 */
public record StaffUpdateRequest(
    @Size(max = 100) String name,
    @Email @Size(max = 100) String emailString,
    @Size(max = 100) String password) {}
