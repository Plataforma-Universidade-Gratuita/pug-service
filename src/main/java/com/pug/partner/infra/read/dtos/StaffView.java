package com.pug.partner.infra.read.dtos;

import com.pug.identity.infra.read.dtos.AccountView;

/**
 * Data Transfer Object (DTO) representing a read-only, consolidated view of a Staff member.
 * <p>
 * Following CQRS principles, this record is used exclusively for returning queried data
 * to the client. It flattens the complex domain relationships by nesting the underlying
 * authentication identity ({@link AccountView}) alongside the details of the partner
 * organization they represent ({@link EntityView}).
 *
 * @param account the read-only projection of the linked authentication account and user
 * @param entity  the read-only projection of the linked partner organization
 */
public record StaffView(AccountView account, EntityView entity) {
}