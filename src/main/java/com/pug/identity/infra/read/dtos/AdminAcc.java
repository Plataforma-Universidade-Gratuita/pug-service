package com.pug.identity.infra.read.dtos;

import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.persistence.AdminEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Internal Data Transfer Object (DTO) used exclusively for JPA tuple projections.
 *
 * <p>This record acts as an intermediate data structure during complex database queries. By
 * fetching both the {@link AdminEntity} and its linked {@link AccountEntity} in a single query
 * projection, it prevents N+1 select performance issues before the data is ultimately mapped into
 * the final, client-facing {@link AdminView}.
 *
 * @param admin the retrieved administrator persistence entity
 * @param account the retrieved account persistence entity linked to the administrator
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record AdminAcc(AdminEntity admin, AccountEntity account) {}
