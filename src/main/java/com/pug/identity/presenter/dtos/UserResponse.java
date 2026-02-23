package com.pug.identity.presenter.dtos;

import com.pug.shared.presenter.dtos.AuditInfoResponse;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing the response for account-related operations.
 *
 * <p>This record encapsulates the essential information about a account, including their unique
 * identifier, CPF (Brazilian individual taxpayer registry identification), name, and timestamps for
 * creation and last update. The formatted versions of the CPF and timestamps are also included for
 * easier presentation in the API responses.
 *
 * @param id the unique identifier of the account
 * @param cpf the CPF number of the account
 * @param cpfFormatted the formatted CPF number for display purposes
 * @param name the name of the account
 * @param auditInfo the audit information containing creation and update timestamps, along with
 *     their formatted string representations
 */
public record UserResponse(
    UUID id, String cpf, String cpfFormatted, String name, AuditInfoResponse auditInfo) {}
