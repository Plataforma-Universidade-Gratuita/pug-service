package com.pug.partner.infra.read.dtos;

import com.pug.geo.infra.persistence.CityEntity;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.partner.infra.persistence.EntityEntity;
import com.pug.partner.infra.persistence.StaffEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Record representing a combination of StaffEntity, AccountEntity, EntityEntity, and CityEntity,
 * typically used in JPA projections to simplify data retrieval for StaffView.
 *
 * @param staff the StaffEntity.
 * @param account the AccountEntity.
 * @param entity the EntityEntity.
 * @param city the CityEntity.
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record StaffAcc(
    StaffEntity staff, AccountEntity account, EntityEntity entity, CityEntity city) {}
