package com.pug.identity.presenter.dtos;

import com.pug.shared.domain.enums.AccountType;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * UserResponse record.
 *
 * @param userId the user ID
 * @param cpf the CPF number
 * @param name the name of the user
 * @param email the email of the user
 * @param accountType the type of account
 * @param accountTypeLabel the label for the account type
 * @param createdAt the creation date and time
 * @param createdAtLabel the label for the creation date and time
 */
public record UserResponse(
    UUID userId,
    String cpf,
    String name,
    String email,
    AccountType accountType,
    String accountTypeLabel,
    OffsetDateTime createdAt,
    String createdAtLabel) {}
