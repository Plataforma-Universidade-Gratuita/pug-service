package br.org.catolicasc.pug.partner.constants;

import br.org.catolicasc.pug.shared.constants.ApiVersions;

/**
 * Centralizes the canonical HTTP route strings exposed by the partner module.
 *
 * <p>These constants define the public REST contract for partner entities and staff management
 * under the versioned API namespace.
 */
public final class PartnerApiPaths {

  /** Shared prefix for the current public API version. */
  public static final String VERSION = ApiVersions.V1;

  /** Relative item route fragment used by entity and staff item endpoints. */
  public static final String ITEM = "/{id}";

  /** Relative self route fragment used by authenticated staff endpoints. */
  public static final String SELF = "/me";

  /** Relative route fragment for listing cities referenced by partner entities. */
  public static final String ENTITY_CITIES_SEGMENT = "/cities";

  /** Root collection endpoint for partner organizations. */
  public static final String ENTITIES = VERSION + "/partners/entities";

  /** Item endpoint for a specific partner organization. */
  public static final String ENTITY_BY_ID = ENTITIES + ITEM;

  /** Collection endpoint for cities referenced by partner organizations. */
  public static final String ENTITY_CITIES = ENTITIES + ENTITY_CITIES_SEGMENT;

  /** Root collection endpoint for partner staff accounts. */
  public static final String STAFF = VERSION + "/partners/staff";

  /** Item endpoint for a specific partner staff account. */
  public static final String STAFF_BY_ID = STAFF + ITEM;

  /** Self endpoint for the currently authenticated partner staff account. */
  public static final String STAFF_ME = STAFF + SELF;

  /** Private constructor to prevent instantiation. */
  private PartnerApiPaths() {}
}
