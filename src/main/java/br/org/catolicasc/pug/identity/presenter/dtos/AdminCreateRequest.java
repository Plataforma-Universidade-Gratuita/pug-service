package br.org.catolicasc.pug.identity.presenter.dtos;

import br.org.catolicasc.pug.shared.domain.enums.Campi;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for creating a new Administrator.
 *
 * <p>This record applies Jakarta Bean Validation constraints to ensure the initial data is
 * structurally sound before it reaches the application service layer. It acts as an aggregate
 * payload that will eventually be split to create the User, Account, and Admin records.
 *
 * @param cpfString the raw 11-digit CPF string (must not be blank)
 * @param name the full name of the administrator (must not be blank and max 150 characters)
 * @param emailString the email address for the administrator's account (must not be blank)
 * @param password the requested password (must not be blank, between 8 and 255 characters)
 * @param campus the designated university campus enum (must not be null)
 */
public record AdminCreateRequest(
    @NotBlank String cpfString,
    @NotBlank @Size(max = 150) String name,
    @NotBlank String emailString,
    @NotBlank @Size(max = 255) String password,
    @NotNull Campi campus) {}
