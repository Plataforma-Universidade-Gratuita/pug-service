package com.pug.partner.infra.read.dtos;

import com.pug.geo.infra.persistence.CityEntity;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.partner.infra.persistence.EntityEntity;
import com.pug.partner.infra.persistence.StaffEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Internal Data Transfer Object (DTO) used exclusively for JPA tuple projections.
 * <p>
 * This record acts as an intermediate data structure during complex cross-domain queries.
 * By fetching the {@link StaffEntity} and all its required associations (Account, Entity, City)
 * in a single query projection, it prevents N+1 select performance issues before the data
 * is ultimately mapped into the final, client-facing {@link StaffView}.
 *
 * @param staff   the retrieved staff persistence entity
 * @param account the retrieved account persistence entity linked to the staff member
 * @param entity  the retrieved partner entity persistence record
 * @param city    the retrieved city persistence entity linked to the partner entity
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record StaffAcc(
        StaffEntity staff, AccountEntity account, EntityEntity entity, CityEntity city) {
}