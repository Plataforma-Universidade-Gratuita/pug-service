package br.org.catolicasc.pug.identity.presenter.dtos;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO used to update the activation status of an existing account.
 *
 * @param active the activation flag that should be applied to the targeted account
 */
public record AccountStatusRequest(@NotNull Boolean active) {}
