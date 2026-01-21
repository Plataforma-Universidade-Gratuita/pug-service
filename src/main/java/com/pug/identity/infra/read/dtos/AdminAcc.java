package com.pug.identity.infra.read.dtos;

import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.persistence.AdminEntity;

/**
 * Record representing a combination of AdminEntity and AccountEntity, typically used in JPA
 * projections to simplify data retrieval.
 *
 * @param admin the AdminEntity.
 * @param account the AccountEntity.
 */
public record AdminAcc(AdminEntity admin, AccountEntity account) {}
