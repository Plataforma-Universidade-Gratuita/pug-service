package br.org.catolicasc.pug.identity.constants;

/**
 * Centralizes the canonical HTTP route strings exposed by the identity module.
 *
 * <p>These constants define the public REST contract for authentication, accounts, admins, and
 * users under the versioned API namespace.
 */
public final class IdentityApiPaths {

  /** Root collection endpoint for authentication actions. */
  public static final String AUTH = "/v1/auth";

  /** Root collection endpoint for user identity records. */
  public static final String USERS = "/v1/identity/users";

  /** Root collection endpoint for authentication accounts. */
  public static final String ACCOUNTS = "/v1/identity/accounts";

  /** Root collection endpoint for administrator accounts. */
  public static final String ADMINS = "/v1/identity/admins";

  /** Private constructor to prevent instantiation. */
  private IdentityApiPaths() {}
}
