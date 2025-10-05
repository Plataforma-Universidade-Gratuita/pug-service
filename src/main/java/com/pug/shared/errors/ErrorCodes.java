package com.pug.shared.errors;

public final class ErrorCodes {
  private ErrorCodes() {}

  public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
  public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
  public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
  public static final String USER_DUPLICATE_CPF = "USER_DUPLICATE_CPF";
  public static final String ROLE_DUPLICATE_EMAIL = "ROLE_DUPLICATE_EMAIL";
  public static final String USER_ALREADY_REGISTERED_AS_FORMER_STUDENT =
      "USER_ALREADY_REGISTERED_AS_FORMER_STUDENT";
  public static final String ROLE_NOT_FOUND = "ROLE_NOT_FOUND";

  public static String bundleKey(String code) {
    return switch (code) {
      case USER_DUPLICATE_CPF -> "error.user.duplicate_cpf";
      case USER_NOT_FOUND -> "error.user.not_found";
      case VALIDATION_ERROR -> "error.validation";
      case INTERNAL_ERROR -> "error.internal";
      case ROLE_DUPLICATE_EMAIL -> "error.role.duplicate_email";
      case USER_ALREADY_REGISTERED_AS_FORMER_STUDENT ->
          "error.user.already_registered_as_former_student";
      case ROLE_NOT_FOUND -> "error.role.not_found";
      default -> code;
    };
  }
}
