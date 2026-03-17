package com.pug.partner.presenter;

import com.pug.identity.service.PasswordService;
import com.pug.partner.domain.Staff;
import com.pug.partner.infra.read.dtos.StaffView;
import com.pug.partner.presenter.dtos.StaffCreateRequest;
import com.pug.partner.presenter.dtos.StaffResponse;
import com.pug.partner.presenter.dtos.StaffUpdateRequest;
import com.pug.partner.presenter.mappers.StaffPresenter;
import com.pug.partner.service.StaffReadService;
import com.pug.partner.service.StaffService;
import com.pug.partner.service.utils.ExceptionHelper;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.utils.PresenterUtils;
import com.pug.shared.utils.StringUtils;
import com.pug.shared.validation.UuidV7;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * REST API Resource controller for managing Partner Staff privileges.
 *
 * <p>This class exposes endpoints to assign, retrieve, update, and revoke organizational roles for
 * authentication accounts. It acts as the HTTP entry point, delegating queries to the {@link
 * StaffReadService} and commands to the {@link StaffService}, adhering to CQRS principles.
 */
@ApplicationScoped
@Path("/partners/staff")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "STAFF"})
public class StaffResource {

  @Inject StaffService writeService;
  @Inject StaffReadService readService;
  @Inject PasswordService passwordService;
  @Inject I18n i18n;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Retrieves a specific staff member by their linked account ID.
   *
   * @param id the unique identifier (UUIDv7) of the staff member's account
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     StaffResponse}
   * @throws ResourceNotFoundException if the staff assignment is not found
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    StaffView v = readService.getViewByAccountId(id);
    StaffResponse body = StaffPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a specific staff member by their registered email address.
   *
   * @param emailRaw the exact email string of the staff member
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     StaffResponse}
   * @throws AppValidationException if the provided email is malformed
   * @throws ResourceNotFoundException if no staff member is found with the given email
   */
  @GET
  @Path("/by-email/{email}")
  public Response getByEmail(@PathParam("email") @NotNull String emailRaw) {
    StaffView v = readService.getViewByEmail(emailRaw);
    StaffResponse out = StaffPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(out)).build();
  }

  /**
   * Retrieves the staff profile details of the currently authenticated user.
   *
   * <p>Extracts the account ID directly from the JWT claims, ensuring staff members can only
   * request their own data.
   *
   * @param identity the injected {@link SecurityIdentity} containing the active JWT
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     StaffResponse}
   * @throws jakarta.ws.rs.NotAuthorizedException if the token is missing or lacks the {@code
   *     accountId} claim
   */
  @GET
  @Path("/me")
  @Authenticated
  public Response getMe(@Context SecurityIdentity identity) {
    if (identity.isAnonymous()) {
      throw ExceptionHelper.unauthorized();
    }

    JsonWebToken jwt = (JsonWebToken) identity.getPrincipal();
    String accountIdClaim = jwt.getClaim("accountId");

    if (accountIdClaim == null) {
      throw ExceptionHelper.unauthorized();
    }

    UUID accountId = UUID.fromString(accountIdClaim);
    StaffView v = readService.getViewByAccountId(accountId);
    StaffResponse body = StaffPresenter.toResponse(v, locale(), i18n);

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a collection of staff members.
   *
   * <p>If the optional {@code q} parameter is provided, it executes a full-text search against the
   * personal names of the staff. If omitted, it returns an unfiltered list of all staff.
   *
   * @param query the optional search query string
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     StaffResponse}
   */
  @GET
  public Response list(@QueryParam("q") String query) {
    List<StaffView> views =
        StringUtils.isNotEmpty(query) ? readService.search(query) : readService.listViews();

    List<StaffResponse> list =
        views.stream()
            .map(v -> StaffPresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /**
   * Retrieves a collection of staff members filtered by the user's exact CPF.
   *
   * @param cpfRaw the raw 11-digit numeric CPF string
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     StaffResponse}
   * @throws AppValidationException if the provided CPF is malformed
   */
  @GET
  @Path("/by-cpf/{cpf}")
  public Response listByCpf(@PathParam("cpf") String cpfRaw) {
    List<StaffResponse> list =
        readService.listViewsByCpf(cpfRaw).stream()
            .map(v -> StaffPresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());
    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /**
   * Retrieves a collection of staff members assigned to a specific partner entity.
   *
   * @param entityId the unique identifier (UUID) of the partner entity
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     StaffResponse}
   */
  @GET
  @Path("/by-entity/{entityId}")
  public Response listByEntity(@PathParam("entityId") @UuidV7 UUID entityId) {
    List<StaffResponse> list =
        readService.listViewsByEntityId(entityId).stream()
            .map(v -> StaffPresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());
    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /**
   * Registers a new staff member and assigns them to a partner organization.
   *
   * <p>This endpoint processes an aggregated payload, automatically handling the provisioning of
   * the underlying user and authentication account within a single transaction.
   *
   * @param req the validated {@link StaffCreateRequest} containing the identity, credentials, and
   *     entity ID
   * @return an HTTP 201 Created response containing a {@code Location} header and the created
   *     {@link StaffResponse}
   * @throws DuplicateResourceException if an account with the email already exists or is already
   *     assigned to the entity
   * @throws com.pug.shared.exceptions.BusinessRuleException if the account is already assigned to a
   *     different entity
   * @throws AppValidationException if input validation fails at the domain level
   * @throws ResourceNotFoundException if the associated partner entity is not found
   */
  @POST
  public Response create(@Valid StaffCreateRequest req) {
    String hashedPassword = passwordService.hash(req.password());
    var cmd = StaffPresenter.toCommand(req, hashedPassword);
    Staff staff = writeService.save(cmd);

    StaffView v = readService.getViewByAccountId(staff.getAccountId());
    StaffResponse out = StaffPresenter.toResponse(v, locale(), i18n);
    URI location = uri.getAbsolutePathBuilder().path(staff.getAccountId().toString()).build();

    return Response.created(location).entity(ApiEnvelope.created(out)).build();
  }

  /**
   * Partially updates a staff member's account and personal details.
   *
   * <p>Note: The staff member's linked entity ID and account ID remain immutable. Omitting fields
   * in the request payload will result in those fields retaining their current state in the
   * database.
   *
   * @param id the unique identifier (UUIDv7) of the staff member's account
   * @param req the validated {@link StaffUpdateRequest} containing the modified data
   * @return an HTTP 200 OK response containing the updated {@link StaffResponse}
   * @throws ResourceNotFoundException if the staff member is not found
   * @throws DuplicateResourceException if the updated email conflicts with an existing account
   * @throws AppValidationException if input validation fails
   */
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid StaffUpdateRequest req) {
    String hashedPassword = req.password() != null ? passwordService.hash(req.password()) : null;
    var cmd = StaffPresenter.toCommand(req, hashedPassword);
    writeService.update(id, cmd);

    StaffView v = readService.getViewByAccountId(id);
    StaffResponse body = StaffPresenter.toResponse(v, locale(), i18n);

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Gracefully deactivates a staff member's account.
   *
   * <p>This disables Login.bru capabilities and system access without destroying the underlying
   * records, maintaining historical referential integrity.
   *
   * @param id the unique identifier (UUIDv7) of the staff member's account
   * @return an HTTP 200 OK response with an empty payload indicating successful deactivation
   */
  @PATCH
  @Path("/{id}/deactivate")
  public Response deactivate(@PathParam("id") @UuidV7 UUID id) {
    writeService.deactivate(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  /**
   * Permanently removes a staff member and their underlying account from the system.
   *
   * @param id the unique identifier (UUIDv7) of the staff member's account to delete
   * @return an HTTP 200 OK response with an empty data payload indicating successful deletion
   * @throws com.pug.shared.exceptions.BusinessRuleException if the staff member has validated
   *     attendances or created projects
   */
  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  /**
   * Helper method to determine the preferred locale from the incoming request headers.
   *
   * @return the resolved {@link Locale} to be used for formatting responses
   */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
