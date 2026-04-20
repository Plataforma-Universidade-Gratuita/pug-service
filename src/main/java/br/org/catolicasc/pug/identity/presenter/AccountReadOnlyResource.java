package br.org.catolicasc.pug.identity.presenter;

import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.identity.presenter.dtos.AccountResponse;
import br.org.catolicasc.pug.identity.presenter.mappers.AccountPresenter;
import br.org.catolicasc.pug.identity.service.AccountReadService;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
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
 * REST endpoint for read-only operations on accounts.
 *
 * <p>Provides endpoints to retrieve individual accounts or lists of accounts. Accessible primarily
 * by users with the ADMIN role, except for specific endpoints.
 */
@Path("/identity/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class AccountReadOnlyResource {

  @Inject AccountReadService readService;

  @Inject I18n i18n;

  @Inject AuthService authService;

  @Context HttpHeaders headers;

  /**
   * Retrieves a specific account by its unique identifier.
   *
   * <p>Finds and returns the account matching the provided {@link UUID}.
   *
   * @param id the {@link UUID} of the account to retrieve
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     AccountResponse}
   */
  @GET
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    var body = AccountPresenter.toResponse(readService.getViewById(id), locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a specific account by its email address.
   *
   * <p>Finds and returns the account matching the provided email.
   *
   * @param emailRaw the email address of the account to retrieve
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     AccountResponse}
   */
  @GET
  @Path("by-email/{email}")
  public Response getByEmail(@PathParam("email") @NotNull String emailRaw) {
    var body = AccountPresenter.toResponse(readService.getViewByEmail(emailRaw), locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves the account details of the currently authenticated user.
   *
   * <p>Extracts the account ID directly from the JWT claims via {@link AuthService}, ensuring users
   * can only request their own account data. Accessible by any authenticated role.
   *
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     AccountResponse}
   * @throws jakarta.ws.rs.NotAuthorizedException if the token is missing or lacks the {@code
   *     accountId} claim
   */
  @GET
  @Path("me")
  @Authenticated
  public Response getMe() {
    UUID accountId = authService.getCurrentAccountId();
    var body = AccountPresenter.toResponse(readService.getViewById(accountId), locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a list of accounts, optionally filtered by a search query.
   *
   * <p>If a query string is provided, it searches for matching accounts; otherwise, it lists all
   * available accounts.
   *
   * @param query an optional search string to filter accounts
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a {@link List} of {@link
   *     AccountResponse}
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
   * Retrieves a list of accounts associated with a specific CPF.
   *
   * <p>Finds and returns all accounts that match the provided CPF.
   *
   * @param cpfRaw the CPF to filter the accounts by
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a {@link List} of {@link
   *     AccountResponse}
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
   * Determines the appropriate {@link Locale} based on the request headers.
   *
   * @return the resolved {@link Locale}
   */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
