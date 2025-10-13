package com.pug.identity.domain;

public final class IdentityErrorCodes {
  private IdentityErrorCodes() {}

  public static final String IDENTITY_CPF_INVALID = "IDENTITY_CPF_INVALID";
  public static final String IDENTITY_CPF_REQUIRED = "IDENTITY_CPF_REQUIRED";
  public static final String IDENTITY_NAME_REQUIRED = "IDENTITY_NAME_REQUIRED";
  public static final String IDENTITY_NAME_TOO_LONG = "IDENTITY_NAME_TOO_LONG";
  public static final String IDENTITY_NOT_FOUND = "IDENTITY_NOT_FOUND";
  public static final String IDENTITY_CPF_ALREADY_IN_USE = "IDENTITY_CPF_ALREADY_IN_USE";
}
