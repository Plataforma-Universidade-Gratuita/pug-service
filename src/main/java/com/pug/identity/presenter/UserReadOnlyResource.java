package com.pug.identity.presenter;

import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.presenter.dtos.UserResponse;
import com.pug.identity.presenter.mappers.UserPresenter;
import com.pug.identity.service.IUserReadService;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.presenter.dtos.BulkCreateResult;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** REST resource for reading user information. */
@Path("/identity/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserReadOnlyResource {

  @Inject IUserReadService readService;

  @Context HttpHeaders headers;

  /**
   * Picks the best locale from the request headers.
   *
   * @return the selected locale.
   */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }

  /**
   * Retrieves a user by their unique identifier.
   *
   * @param id the UUID of the user
   * @return the response containing the user data
   * @throws ResourceNotFoundException if no user with the given ID is found.
   */
  @GET
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    UserResponse body = UserPresenter.toResponse(readService.getViewById(id), locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Lists all users.
   *
   * @return the response containing the list of users
   */
  @GET
  public Response list() {
    List<UserResponse> list =
        readService.listViews().stream().map(v -> UserPresenter.toResponse(v, locale())).toList();
    return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(list))).build();
  }

  /**
   * Retrieves a user by their CPF.
   *
   * @param cpfRaw the raw CPF string of the user
   * @return the response containing the user data
   * @throws AppValidationException if the provided CPF is malformed.
   * @throws ResourceNotFoundException if no user with the given CPF is found.
   */
  @GET
  @Path("by-cpf/{cpf}")
  public Response getByCpf(@PathParam("cpf") @NotNull String cpfRaw) {
    Cpf cpfVO;
    cpfVO = new Cpf(cpfRaw);
    UserResponse body =
        UserPresenter.toResponse(readService.getViewByCpf(cpfVO.toString()), locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Lists users by their name matching the query.
   *
   * @param query the name query to search for
   * @return the response containing the list of users matching the name query
   */
  @GET
  @Path("by-name")
  public Response listByName(@QueryParam("q") String query) {
    var body = new ArrayList<UserResponse>();
    if (StringUtils.isEmpty(query)) {
      return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(body))).build();
    }
    List<UserResponse> list =
        readService.search(query).stream().map(v -> UserPresenter.toResponse(v, locale())).toList();
    return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(list))).build();
  }
}
