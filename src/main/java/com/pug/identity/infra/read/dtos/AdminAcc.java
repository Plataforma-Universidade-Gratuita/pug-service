package com.pug.identity.infra.read.dtos;

import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.persistence.AdminEntity;

/**
 * Admin with Account for queries.
 * @param admin the admin entity
 * @param account the account entity
 */
public record AdminAcc(AdminEntity admin, AccountEntity account) {
}