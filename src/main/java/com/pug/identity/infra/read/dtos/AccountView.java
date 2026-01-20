package com.pug.identity.infra.read.dtos;

import com.pug.shared.domain.enums.AccountType;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing an account view.
 *
 * @param id          the unique identifier of the account
 * @param user        the user associated with the account (a UserView DTO)
 * @param email       the email address of the account
 * @param accountType the type of account
 * @param createdAt   the timestamp when the account was created
 */
public record AccountView(
        UUID id, UserView user, String email, AccountType accountType, OffsetDateTime createdAt) {
}