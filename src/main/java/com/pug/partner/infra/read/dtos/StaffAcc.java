package com.pug.partner.infra.read.dtos;

import com.pug.geo.infra.persistence.CityEntity;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.partner.infra.persistence.EntityEntity;
import com.pug.partner.infra.persistence.StaffEntity;

/**
 * Staff with Account, Entity and City for queries.
 *
 * @param staff   the staff entity
 * @param account the account entity
 * @param entity  the entity entity
 * @param city    the city entity
 */
public record StaffAcc(
        StaffEntity staff,
        AccountEntity account,
        EntityEntity entity,
        CityEntity city) {
}
