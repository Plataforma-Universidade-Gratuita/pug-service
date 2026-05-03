package br.org.catolicasc.pug.geo.constants;

import br.org.catolicasc.pug.shared.constants.ApiVersions;

/** Centralized HTTP path constants for the geo module resources. */
public final class GeoApiPaths {

  public static final String VERSION = ApiVersions.V1;

  public static final String CITIES = "/v1/geo/cities";

  public static final String BY_ID = "/{id}";
  public static final String BY_IBGE = "/by-ibge/{ibgeCode}";

  /** Private constructor to prevent instantiation. */
  private GeoApiPaths() {}
}
