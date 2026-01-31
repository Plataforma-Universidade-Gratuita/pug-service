package com.pug.identity.infra.read.dtos;

import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.persistence.AdminEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Record representing a combination of AdminEntity and AccountEntity, typically used in JPA
 * projections to simplify data retrieval.
 *
 * @param admin the AdminEntity.
 * @param account the AccountEntity.
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record AdminAcc(AdminEntity admin, AccountEntity account) {}
