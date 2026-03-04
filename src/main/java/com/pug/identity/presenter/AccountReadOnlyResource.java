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
 * REST API Resource controller for read-only operations on authentication Accounts.
 * <p>
 * This class exposes endpoints to retrieve existing accounts. It acts as the HTTP entry
 * point, delegating queries to the {@link AccountReadService} and adhering to CQRS principles.
 * Write operations for accounts are intentionally handled through aggregate-specific resources
 * (e.g., {@link AdminResource} or Student integrations) to enforce strict business rules.
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
   * Retrieves a specific account by its unique UUID identifier.
   *
   * @param id the unique identifier (UUIDv7) of the requested account
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link AccountResponse}
   * @throws ResourceNotFoundException if no account with the given ID is found
   */
  @GET
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    var body = AccountPresenter.toResponse(readService.getViewById(id), locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a specific account by its registered email address.
   *
   * @param emailRaw the exact email string of the account
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link AccountResponse}
   * @throws AppValidationException    if the provided email is malformed
   * @throws ResourceNotFoundException if no account with the given email is found
   */
  @GET
  @Path("by-email/{email}")
  public Response getByEmail(@PathParam("email") @NotNull String emailRaw) {
    var body = AccountPresenter.toResponse(readService.getViewByEmail(emailRaw), locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a collection of accounts.
   * <p>
   * If the optional {@code q} parameter is provided, it executes a full-text search against
   * the associated users' names. If omitted, it returns an unfiltered list of all accounts.
   *
   * @param query the optional search query string used to filter by user name
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link AccountResponse}
   */
  @GET
  public Response list(@QueryParam("q") String query) {
    List<AccountView> views;

    if (StringUtils.isNotEmpty(query)) {
      views = readService.search(query);
    } else {
      views = readService.listViews();
    }

    List<AccountResponse> list =
            views.stream()
                    .map(v -> AccountPresenter.toResponse(v, locale(), i18n))
                    .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /**
   * Retrieves a collection of accounts linked to a specific user's CPF.
   *
   * @param cpfRaw the raw 11-digit numeric CPF string
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link AccountResponse}
   * @throws AppValidationException if the provided CPF is malformed
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
   * Helper method to determine the preferred locale from the incoming request headers.
   */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}