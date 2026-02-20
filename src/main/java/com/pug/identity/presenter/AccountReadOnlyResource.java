package com.pug.identity.presenter;

import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.identity.presenter.dtos.AccountResponse;
import com.pug.identity.presenter.mappers.AccountPresenter;
import com.pug.identity.service.AccountReadService;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.i18n.I18n;
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
import java.util.stream.Collectors;

/**
 * REST resource for read-only operations on accounts.
 */
@Path("/identity/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountReadOnlyResource {

  @Inject
  AccountReadService readService;
  @Inject
  I18n i18n;

  @Context
  HttpHeaders headers;

  /**
   * Retrieves an account by its ID.
   *
   * @param id the UUIDv7 of the account.
   * @return the account response wrapped in an ApiEnvelope.
   * @throws ResourceNotFoundException if no account with the given ID is found.
   */
  @GET
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    var body = AccountPresenter.toResponse(readService.getViewById(id), locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Lists accounts.
   * <p>
   * If the 'q' query parameter is provided, performs a search by user name.
   * Otherwise, returns all accounts.
   * </p>
   *
   * @param query optional name query to search for.
   * @return the response containing the list of accounts.
   */
  @GET
  public Response list(@QueryParam("q") String query) {
    List<AccountView> views;

    if (StringUtils.isNotEmpty(query)) {
      views = readService.search(query);
    } else {
      views = readService.listViews();
    }

    List<AccountResponse> list = views.stream()
            .map(v -> AccountPresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /**
   * Retrieves an account by its email.
   *
   * @param emailRaw the raw email string of the account.
   * @return the account response wrapped in an ApiEnvelope.
   * @throws AppValidationException    if the provided email is malformed.
   * @throws ResourceNotFoundException if no account with the given email is found.
   */
  @GET
  @Path("by-email/{email}")
  public Response getByEmail(@PathParam("email") @NotNull String emailRaw) {
    var body = AccountPresenter.toResponse(readService.getViewByEmail(emailRaw), locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Lists accounts by CPF.
   *
   * @param cpfRaw the raw CPF string of the accounts.
   * @return a list of account responses wrapped in an ApiEnvelope.
   * @throws AppValidationException    if the provided CPF is malformed.
   * @throws ResourceNotFoundException if associated user data is missing for any found account.
   */
  @GET
  @Path("by-cpf/{cpf}")
  public Response listByCpf(@PathParam("cpf") @NotNull String cpfRaw) {
    List<AccountResponse> list =
            readService.listViewsByCpf(cpfRaw).stream()
                    .map(v -> AccountPresenter.toResponse(v, locale(), i18n))
                    .collect(Collectors.toList());
    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /**
   * Picks the best locale from the Accept-Language header.
   */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}