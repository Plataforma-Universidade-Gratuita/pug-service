package com.pug.partner.infra.read.dtos;

import com.pug.identity.infra.read.dtos.AccountView;

/**
 * View representation of a Staff member.
 *
 * @param account the account view associated with the staff member.
 * @param entity  the entity view associated with the staff member.
 */
public record StaffView(AccountView account, EntityView entity) {
}