package com.pug.identity.service.dtos;

import com.pug.shared.domain.enums.Campi;

/**
 * Data Transfer Object (DTO) acting as an application command to grant new Administrator
 * privileges.
 *
 * <p>This record encapsulates the raw input data required by the application service to instantiate
 * a new {@link com.pug.identity.domain.Admin} aggregate, cascading down to create the necessary
 * authentication account and user identity in a single transaction.
 *
 * @param accountCommand the nested command containing the data to create the underlying
 *     authentication account
 * @param campus the designated university campus for the administrator's jurisdiction
 */
public record AdminCreateCommand(AccountCreateCommand accountCommand, Campi campus) {}
