package br.org.catolicasc.pug.identity.presenter;

import br.org.catolicasc.pug.identity.constants.IdentityApiPaths;
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
@Path(IdentityApiPaths.ACCOUNTS)
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
   * Retrieves accounts, optionally filtered by query parameters.
   *
   * <p>When {@code email} is provided, this endpoint returns the single account linked to that
   * email. When {@code cpf} is provided, it returns the accounts associated with that CPF. If
   * neither identifier is present, it falls back to name-based search with {@code q} or lists all
   * accounts when no filters are supplied.
   *
   * @param query the optional search string used to filter accounts by text
   * @param emailRaw the optional email used to retrieve a single account
   * @param cpfRaw the optional CPF used to retrieve the accounts associated with a user
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with either a single {@link
   *     AccountResponse} or a {@link List} of {@link AccountResponse}
   */
  @GET
  public Response list(
      @QueryParam("q") String query,
      @QueryParam("email") String emailRaw,
      @QueryParam("cpf") String cpfRaw) {
    if (StringUtils.isNotEmpty(emailRaw)) {
      var body = AccountPresenter.toResponse(readService.getViewByEmail(emailRaw), locale(), i18n);
      return Response.ok(ApiEnvelope.ok(body)).build();
    }

    if (StringUtils.isNotEmpty(cpfRaw)) {
      List<AccountResponse> list =
          readService.listViewsByCpf(cpfRaw).stream()
              .map(v -> AccountPresenter.toResponse(v, locale(), i18n))
              .collect(Collectors.toList());
      return Response.ok(ApiEnvelope.ok(list)).build();
    }

    List<AccountView> views =
        StringUtils.isNotEmpty(query) ? readService.search(query) : readService.listViews();
    List<AccountResponse> list =
        views.stream()
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
