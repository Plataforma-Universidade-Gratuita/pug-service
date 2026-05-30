package br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise;

import jakarta.validation.constraints.Pattern;

/**
 * Request DTO used by the area-of-expertise complex-search endpoint.
 *
 * @param name optional area-of-expertise name fragment used in a {@code like} filter
 */
public record AreaOfExpertiseComplexSearchRequest(@Pattern(regexp = ".*\\S.*") String name) {}
