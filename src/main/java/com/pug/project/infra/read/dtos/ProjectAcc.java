package com.pug.project.infra.read.dtos;

import com.pug.geo.infra.persistence.CityEntity;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.partner.infra.persistence.EntityEntity;
import com.pug.project.infra.persistence.ProjectEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Internal Data Transfer Object (DTO) used exclusively for JPA tuple projections.
 *
 * <p>Acts as an intermediate data structure during complex cross-domain queries. By fetching the
 * {@link ProjectEntity} and all its required associations (Partner Entity, City, Creator Account,
 * Creator User) in a single query projection, it prevents N+1 select performance issues.
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record ProjectAcc(
    ProjectEntity project,
    EntityEntity entity,
    CityEntity city,
    AccountEntity creatorAccount,
    UserEntity creatorUser) {}
