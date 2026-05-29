package br.org.catolicasc.pug.identity.presenter.dtos;

import br.org.catolicasc.pug.shared.domain.enums.Campi;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for partially updating an existing
 * Administrator.
 *
 * <p>Because updates can be partial, all fields in this record are inherently optional. If a field
 * is provided as {@code null} or omitted from the JSON payload, the application service will ignore
 * it and retain the existing value for that specific attribute.
 *
 * @param name the new full name, or {@code null} to leave unchanged (if provided, max 150
 *     characters)
 * @param emailString the new email address string, or {@code null} to leave unchanged
 * @param campus the new university campus assignment, or {@code null} to leave unchanged
 */
public record AdminUpdateRequest(
    @Size(max = 150) String name, @JsonProperty("email") String emailString, Campi campus) {}
