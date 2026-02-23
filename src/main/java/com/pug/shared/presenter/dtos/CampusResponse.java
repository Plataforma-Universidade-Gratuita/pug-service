package com.pug.shared.presenter.dtos;

import com.pug.shared.domain.enums.Campi;

/**
 * CampusResponse is a record that represents the response data for a campus.
 *
 * @param campus The enum value representing the campus.
 * @param campusFormatted A formatted string representation of the campus (localized).
 */
public record CampusResponse(Campi campus, String campusFormatted) {}
