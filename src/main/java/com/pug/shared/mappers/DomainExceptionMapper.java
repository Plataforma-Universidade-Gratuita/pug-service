package com.pug.shared.mappers;

import static com.pug.shared.errors.ErrorCodes.CITY_NOT_FOUND;
import static com.pug.shared.errors.ErrorCodes.ENTITY_DUPLICATE_CNPJ;
import static com.pug.shared.errors.ErrorCodes.ENTITY_NOT_FOUND;
import static com.pug.shared.errors.ErrorCodes.ROLE_DUPLICATE_EMAIL;
import static com.pug.shared.errors.ErrorCodes.ROLE_NOT_FOUND;
import static com.pug.shared.errors.ErrorCodes.USER_ALREADY_REGISTERED_AS_FORMER_STUDENT;
import static com.pug.shared.errors.ErrorCodes.USER_DUPLICATE_CPF;
import static com.pug.shared.errors.ErrorCodes.USER_NOT_FOUND;

import com.pug.shared.dtos.ApiError;
import com.pug.shared.dtos.ApiResponse;
import com.pug.shared.errors.DomainException;
import com.pug.shared.errors.ErrorCodes;
import com.pug.shared.i18n.I18n;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;
import java.util.UUID;

@Provider
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {
  @Context HttpHeaders headers;
  @Inject I18n i18n;

  private static final Map<String, Response.Status> STATUS =
      Map.of(
          USER_ALREADY_REGISTERED_AS_FORMER_STUDENT, Response.Status.CONFLICT,
          ENTITY_DUPLICATE_CNPJ, Response.Status.CONFLICT,
          USER_DUPLICATE_CPF, Response.Status.CONFLICT,
          ROLE_DUPLICATE_EMAIL, Response.Status.CONFLICT,
          CITY_NOT_FOUND, Response.Status.NOT_FOUND,
          ROLE_NOT_FOUND, Response.Status.NOT_FOUND,
          USER_NOT_FOUND, Response.Status.NOT_FOUND,
          ENTITY_NOT_FOUND, Response.Status.NOT_FOUND);

  @Override
  public Response toResponse(DomainException ex) {
    var loc = i18n.resolve(headers);
    var code = ex.getCode();
    var msg = i18n.msg(ErrorCodes.bundleKey(code), loc, ex.getArgs());
    var details = buildDetails(ex);

    var body = ApiResponse.error(ApiError.of(code, msg, details));

    return Response.status(STATUS.getOrDefault(code, Response.Status.BAD_REQUEST))
        .entity(body)
        .type(MediaType.APPLICATION_JSON)
        .build();
  }

  private Map<String, Object> buildDetails(DomainException ex) {
    var code = ex.getCode();
    var args = ex.getArgs();
    if (args.length == 1) {
      Object a = args[0];
      return switch (code) {
        case USER_NOT_FOUND -> {
          if (a instanceof UUID u) yield Map.of("id", u);
          yield Map.of("cpf", a);
        }
        case ROLE_NOT_FOUND -> {
          if (a instanceof UUID u) yield Map.of("id", u);
          yield Map.of("email", a);
        }
        case ENTITY_NOT_FOUND -> {
          if (a instanceof UUID u) yield Map.of("id", u);
          yield Map.of("cnpj", a);
        }
        case CITY_NOT_FOUND -> Map.of("ibgeCode", a);
        case USER_DUPLICATE_CPF -> Map.of("cpf", a);
        case ROLE_DUPLICATE_EMAIL -> Map.of("email", a);
        case ENTITY_DUPLICATE_CNPJ -> Map.of("cnpj", a);
        case USER_ALREADY_REGISTERED_AS_FORMER_STUDENT -> Map.of("user", a);
        default -> Map.of();
      };
    }
    return Map.of();
  }
}
