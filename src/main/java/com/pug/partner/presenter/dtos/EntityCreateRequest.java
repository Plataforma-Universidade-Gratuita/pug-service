package com.pug.partner.presenter.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for creating a new Partner Entity.
 * <p>
 * This record applies Jakarta Bean Validation constraints to ensure the initial data
 * is structurally sound before it reaches the application service layer.
 *
 * @param cnpjString the raw 14-digit numeric CNPJ string (must not be blank)
 * @param name       the registered name or corporate reason of the organization (must not be blank and max 150 characters)
 * @param cityId     the unique identifier (UUID) of the city where the organization is located (must not be null)
 * @param address    the physical street address (optional, max 254 characters)
 */
public record EntityCreateRequest(
        @NotBlank String cnpjString,
        @NotBlank @Size(max = 150) String name,
        @NotNull UUID cityId,
        @Size(max = 254) String address) {
}