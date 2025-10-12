package com.pug.shared.errors;

public final class ErrorCodes {
  private ErrorCodes() {}

  public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
  public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
  public static final String USER_CPF_INVALID = "USER_CPF_INVALID";
  public static final String USER_CPF_REQUIRED = "USER_CPF_REQUIRED";
  public static final String USER_NAME_REQUIRED = "USER_NAME_REQUIRED";
  public static final String USER_NAME_TOO_LONG = "USER_NAME_TOO_LONG";
  public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
  public static final String USER_CPF_ALREADY_IN_USE = "USER_CPF_ALREADY_IN_USE";

  public static String bundleKey(String code) {
    return switch (code) {
      case VALIDATION_ERROR -> "error.validation";
      case INTERNAL_ERROR -> "error.internal";
      case USER_CPF_INVALID -> "error.identity.user.cpf.invalid";
      case USER_CPF_REQUIRED -> "error.identity.user.cpf.required";
      case USER_NAME_REQUIRED -> "error.identity.user.name.required";
      case USER_NAME_TOO_LONG -> "error.identity.user.name.toolong";
      case USER_NOT_FOUND -> "error.identity.user.notfound";
      case USER_CPF_ALREADY_IN_USE -> "error.identity.user.cpf.alreadyinuse";
      default -> code;
    };
  }
}
