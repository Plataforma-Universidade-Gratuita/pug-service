package br.org.catolicasc.pug.partner.presenter.dtos;

/**
 * Response DTO used as the content item returned by the staff complex-search endpoint.
 *
 * @param account the lightweight account projection associated with the staff member
 * @param entity the lightweight partner-entity projection associated with the staff member
 */
public record StaffComplexSearchResponse(
    br.org.catolicasc.pug.identity.presenter.dtos.AccountComplexSearchResponse account,
    EntitySimpleComplexSearchResponse entity) {}
