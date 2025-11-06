package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.errors.ErrorCodes;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

/** Mapper para capturar exceções não tratadas e retornar uma resposta de erro genérica. */
@Provider
@ApplicationScoped
public class UncaughtExceptionMapper implements ExceptionMapper<Throwable> {

  @Inject I18n i18n;

  /**
   * Mapeia exceções não tratadas para uma resposta HTTP 500 com uma mensagem de erro genérica.
   *
   * @param ex A exceção capturada.
   * @return Uma resposta HTTP 500 com detalhes do erro.
   */
  @Override
  public Response toResponse(Throwable ex) {
    String msg = i18n.translation(ErrorCodes.INTERNAL_ERROR.getBundleKey());
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(ErrorCodes.INTERNAL_ERROR.toString(), msg, Map.of()))
        .build();
  }
}
