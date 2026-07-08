package br.org.catolicasc.pug.identity.infra.read.dtos;

import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.time.OffsetDateTime;

/**
 * Data Transfer Object (DTO) representing a read-only, consolidated view of an Administrator
 * profile.
 *
 * <p>Following CQRS principles, this record is used exclusively for returning queried data to the
 * client. It flattens the internal domain relationships by nesting the underlying authentication
 * account as an {@link AccountView}, while keeping user details referenced indirectly via the
 * {@code userId} contained in that account view.
 *
 * @param accountView the read-only projection of the linked authentication account (including
 *     userId, email, type and audit info)
 * @param grantedAt the exact timestamp when administrative privileges were granted
 * @param campus the designated university campus where the administrator operates
 */
public record AdminView(AccountView accountView, OffsetDateTime grantedAt, Campi campus) {}
