package br.org.catolicasc.pug.identity.constants;

import br.org.catolicasc.pug.shared.constants.ApiVersions;

/**
 * Centralizes the canonical HTTP route strings exposed by the identity module.
 *
 * <p>These constants define the public REST contract for authentication, accounts, admins, and
 * users under the versioned API namespace.
 */
public final class IdentityApiPaths {

  /** Shared prefix for the current public API version. */
  public static final String VERSION = ApiVersions.V1;

  /** Relative item route fragment used by account, admin, and user item endpoints. */
  public static final String ITEM = "/{id}";

  /** Relative self route fragment used by authenticated identity endpoints. */
  public static final String SELF = "/me";

  /** Relative route fragment for username/password authentication. */
  public static final String LOGIN = "/login";

  /** Relative route fragment for token renewal. */
  public static final String REFRESH = "/refresh";

  /** Relative route fragment for single-session logout. */
  public static final String LOGOUT = "/logout";

  /** Relative route fragment for full-session logout. */
  public static final String LOGOUT_ALL = "/logout-all";

  /** Root collection endpoint for authentication actions. */
  public static final String AUTH = VERSION + "/auth";

  /** Login endpoint for username/password authentication. */
  public static final String AUTH_LOGIN = AUTH + LOGIN;

  /** Refresh endpoint for token renewal. */
  public static final String AUTH_REFRESH = AUTH + REFRESH;

  /** Logout endpoint for single-session revocation. */
  public static final String AUTH_LOGOUT = AUTH + LOGOUT;

  /** Logout endpoint for revoking every session of the current account. */
  public static final String AUTH_LOGOUT_ALL = AUTH + LOGOUT_ALL;

  /** Root collection endpoint for user identity records. */
  public static final String USERS = VERSION + "/identity/users";

  /** Item endpoint for a specific user identity record. */
  public static final String USER_BY_ID = USERS + ITEM;

  /** Self endpoint for the currently authenticated user identity record. */
  public static final String USER_ME = USERS + SELF;

  /** Root collection endpoint for authentication accounts. */
  public static final String ACCOUNTS = VERSION + "/identity/accounts";

  /** Item endpoint for a specific authentication account. */
  public static final String ACCOUNT_BY_ID = ACCOUNTS + ITEM;

  /** Self endpoint for the currently authenticated account. */
  public static final String ACCOUNT_ME = ACCOUNTS + SELF;

  /** Root collection endpoint for administrator accounts. */
  public static final String ADMINS = VERSION + "/identity/admins";

  /** Item endpoint for a specific administrator account. */
  public static final String ADMIN_BY_ID = ADMINS + ITEM;

  /** Self endpoint for the currently authenticated administrator. */
  public static final String ADMIN_ME = ADMINS + SELF;

  /** Private constructor to prevent instantiation. */
  private IdentityApiPaths() {}
}
