package br.org.catolicasc.pug.partner.service.dtos;

import br.org.catolicasc.pug.identity.service.dtos.AccountCreateCommand;
import br.org.catolicasc.pug.partner.domain.Staff;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) acting as an application command to assign a new Staff member.
 *
 * <p>This record encapsulates the raw input data required by the application service to instantiate
 * a new {@link Staff} aggregate, cascading down to provision the necessary authentication account
 * (and potentially the user identity) in a single transaction.
 *
 * @param entityId the unique identifier of the partner organization the staff belongs to
 * @param accountCommand the nested command containing the data to create the underlying
 *     authentication account
 */
public record StaffCreateCommand(UUID entityId, AccountCreateCommand accountCommand) {}
