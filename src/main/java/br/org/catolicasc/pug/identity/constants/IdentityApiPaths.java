package br.org.catolicasc.pug.identity.constants;

import br.org.catolicasc.pug.shared.constants.ApiVersions;

/** Centralized HTTP path constants for the identity module resources. */
public final class IdentityApiPaths {

  public static final String VERSION = ApiVersions.V1;

  public static final String AUTH = "/v1/auth";
  public static final String USERS = "/v1/identity/users";
  public static final String ACCOUNTS = "/v1/identity/accounts";
  public static final String ADMINS = "/v1/identity/admins";

  public static final String BY_ID = "/{id}";
  public static final String ME = "/me";
  public static final String LOGIN = "/login";
  public static final String REFRESH = "/refresh";
  public static final String LOGOUT = "/logout";
  public static final String LOGOUT_ALL = "/logout-all";

  /** Private constructor to prevent instantiation. */
  private IdentityApiPaths() {}
}
