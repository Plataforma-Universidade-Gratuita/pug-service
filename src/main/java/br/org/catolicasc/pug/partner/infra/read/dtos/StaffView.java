package br.org.catolicasc.pug.partner.infra.read.dtos;

import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a read-only, consolidated view of a Staff member.
 *
 * <p>Following CQRS principles, this record is used exclusively for returning queried data to the
 * client. It nests the authentication account projection ({@link AccountView}) and exposes only the
 * identifiers of the linked partner organization and city, allowing the client to resolve
 * additional details on demand via dedicated endpoints.
 *
 * @param account the read-only projection of the linked authentication account
 * @param entityId the unique identifier (UUIDv7) of the partner organization the staff belongs to
 * @param cityId the unique identifier (UUIDv7) of the city where the partner organization is
 *     located
 */
public record StaffView(AccountView account, UUID entityId, UUID cityId) {}
