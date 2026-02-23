package com.pug.identity.presenter;

import com.pug.identity.infra.read.dtos.UserView;
import com.pug.identity.presenter.dtos.UserResponse;
import com.pug.identity.presenter.mappers.UserPresenter;
import com.pug.identity.service.UserReadService;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.utils.PresenterUtils;
import com.pug.shared.utils.StringUtils;
import com.pug.shared.validation.UuidV7;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** REST resource for reading account information. */
@Path("/identity/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserReadOnlyResource {

  @Inject UserReadService readService;

  @Context HttpHeaders headers;

  /**
   * Retrieves a account by their unique identifier.
   *
   * @param id the UUID of the account
   * @return the response containing the account data
   * @throws ResourceNotFoundException if no account with the given ID is found.
   */
  @GET
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    UserResponse body = UserPresenter.toResponse(readService.getViewById(id), locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Lists users.
   *
   * <p>If the 'q' query parameter is provided, performs a search by name. Otherwise, returns all
   * users.
   *
   * @param query optional name query to search for.
   * @return the response containing the list of users.
   */
  @GET
  public Response list(@QueryParam("q") String query) {
    List<UserView> views;

    if (StringUtils.isNotEmpty(query)) {
      views = readService.search(query);
    } else {
      views = readService.listViews();
    }

    List<UserResponse> list =
        views.stream().map(v -> UserPresenter.toResponse(v, locale())).toList();

    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /**
   * Retrieves a account by their CPF.
   *
   * @param cpfRaw the raw CPF string of the account
   * @return the response containing the account data
   * @throws AppValidationException if the provided CPF is malformed.
   * @throws ResourceNotFoundException if no account with the given CPF is found.
   */
  @GET
  @Path("by-cpf/{cpf}")
  public Response getByCpf(@PathParam("cpf") @NotNull String cpfRaw) {
    UserResponse body = UserPresenter.toResponse(readService.getViewByCpf(cpfRaw), locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /** Picks the best locale from the request headers. */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
