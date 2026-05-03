package br.org.catolicasc.pug.partner.constants;

import br.org.catolicasc.pug.shared.constants.ApiVersions;

/** Centralized HTTP path constants for the partner module resources. */
public final class PartnerApiPaths {

  public static final String VERSION = ApiVersions.V1;

  public static final String ENTITIES = "/v1/partner/entities";
  public static final String STAFF = "/v1/partners/staff";

  public static final String BY_ID = "/{id}";
  public static final String ME = "/me";
  public static final String CITIES = "/cities";

  /** Private constructor to prevent instantiation. */
  private PartnerApiPaths() {}
}
