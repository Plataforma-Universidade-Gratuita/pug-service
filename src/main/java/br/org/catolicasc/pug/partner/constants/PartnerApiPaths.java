package br.org.catolicasc.pug.partner.constants;

/**
 * Centralizes the canonical HTTP route strings exposed by the partner module.
 *
 * <p>These constants define the public REST contract for partner entities and staff management
 * under the versioned API namespace.
 */
public final class PartnerApiPaths {

  /** Root collection endpoint for partner organizations. */
  public static final String ENTITIES = "/v1/partners/entities";

  /** Root collection endpoint for partner staff accounts. */
  public static final String STAFF = "/v1/partners/staff";

  /** Private constructor to prevent instantiation. */
  private PartnerApiPaths() {}
}
