package com.pug.identity.infra.read.dtos;

import com.pug.shared.domain.enums.AccountType;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing a user view.
 *
 * @param id the unique identifier of the user
 * @param person the person associated with the user
 * @param email the email address of the user
 * @param accountType the type of account the user has
 * @param createdAt the timestamp when the user was created
 */
public record AccountView(
    UUID id,
    UserView person,
    String email,
    AccountType accountType,
    OffsetDateTime createdAt) {}
