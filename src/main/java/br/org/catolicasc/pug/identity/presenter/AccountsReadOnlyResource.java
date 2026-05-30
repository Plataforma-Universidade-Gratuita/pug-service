package br.org.catolicasc.pug.identity.presenter;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountComplexSearchRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountResponse;
import br.org.catolicasc.pug.identity.presenter.mappers.AccountPresenter;
import br.org.catolicasc.pug.identity.service.AccountsReadService;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountComplexSearchCriteria;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.dtos.PageResponse;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST API resource controller for read-only operations on Accounts.
 *
 * <p>This class exposes endpoints to retrieve existing authentication accounts. It acts as the HTTP
 * entry point, delegating queries to the {@link AccountsReadService} and adhering to CQRS
 * principles.
 */
@Path("/v1/identity/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class AccountsReadOnlyResource {

  @Inject AccountsReadService readService;

  @Inject I18n i18n;

  @Inject AuthService authService;

  @Context HttpHeaders headers;

  /**
   * Retrieves a specific account by its unique UUID identifier.
   *
   * @param id the unique identifier (UUIDv7) of the account
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
   * <p>The account identifier is resolved exclusively from the JWT {@code accountId} claim via
   * {@link AuthService}, ensuring that callers can only access their own account record, regardless
   * of request parameters. This endpoint is available to any authenticated role.
   *
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     AccountResponse}
   * @throws jakarta.ws.rs.NotAuthorizedException if the token is missing or does not contain the
   *     required {@code accountId} claim
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
   * Retrieves accounts, optionally filtered by a collection of identifiers.
   *
   * <p>When one or more {@code ids} query parameters are provided, this endpoint returns only the
   * corresponding accounts. Otherwise, it returns the complete account list ordered according to
   * the underlying query implementation.
   *
   * @param ids the optional account identifiers used to restrict the returned collection
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     AccountResponse}
   */
  @GET
  public Response list(@QueryParam("ids") List<UUID> ids) {
    List<AccountView> views =
        CollectionUtils.isEmpty(ids) ? readService.listViews() : readService.listViewsByIds(ids);
    List<AccountResponse> list =
        views.stream().map(v -> AccountPresenter.toResponse(v, locale(), i18n)).toList();
    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /**
   * Executes paginated account search using the complex-search contract.
   *
   * @param page the zero-based page index
   * @param size the requested page size; {@code 1} returns the full result set in a single page
   * @param request the optional complex-search filters
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the paginated search
   *     result
   */
  @POST
  @Path("search")
  public Response search(
      @QueryParam("page") @DefaultValue("0") @Min(0) int page,
      @QueryParam("size") @DefaultValue("25") @Min(1) int size,
      @Valid AccountComplexSearchRequest request) {
    AccountComplexSearchCriteria criteria =
        request == null
            ? new AccountComplexSearchCriteria(null, null, null, null, null, null, true)
            : new AccountComplexSearchCriteria(
                request.name(),
                request.cpf(),
                request.email(),
                request.accountTypes(),
                request.dateFrom(),
                request.dateTo(),
                request.activeOnly() == null || request.activeOnly());

    var result = readService.search(new PageQuery(page, size), criteria);
    var responseBody =
        new PageResponse<>(
            result.content().stream()
                .map(v -> AccountPresenter.toComplexSearchResponse(v, locale(), i18n))
                .toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages());

    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }

  /** Helper method to determine the preferred locale from the incoming request headers. */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
