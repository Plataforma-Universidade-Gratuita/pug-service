package br.org.catolicasc.pug.partner.presenter.dtos.staff;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for updating an existing Staff
 * member's underlying account and organizational assignment.
 *
 * <p>Because updates can be partial, all fields in this record are optional. When {@code entityId}
 * is provided, the staff member is transferred from the current partner entity to the supplied one.
 *
 * @param name the new name of the staff member, or {@code null} to leave unchanged
 * @param email the new email address, or {@code null} to leave unchanged
 * @param entityId the new partner entity identifier, or {@code null} to preserve the current one
 */
public record StaffUpdateRequest(
    @Size(max = 100) String name,
    @Email @Size(max = 100) @JsonProperty("email") String email,
    UUID entityId) {}
