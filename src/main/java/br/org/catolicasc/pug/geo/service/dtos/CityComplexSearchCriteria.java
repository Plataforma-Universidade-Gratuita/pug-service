package br.org.catolicasc.pug.geo.service.dtos;

/**
 * Internal criteria object used to encapsulate filters for city search operations within the
 * service layer.
 *
 * @param name the city name filter used to narrow search results
 */
public record CityComplexSearchCriteria(String name) {}
