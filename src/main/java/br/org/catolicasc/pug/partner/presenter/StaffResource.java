package br.org.catolicasc.pug.partner.presenter;

import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.identity.service.PasswordService;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.partner.presenter.dtos.StaffCreateRequest;
import br.org.catolicasc.pug.partner.presenter.dtos.StaffResponse;
import br.org.catolicasc.pug.partner.presenter.dtos.StaffUpdateRequest;
import br.org.catolicasc.pug.partner.presenter.mappers.StaffPresenter;
import br.org.catolicasc.pug.partner.service.StaffReadService;
import br.org.catolicasc.pug.partner.service.StaffService;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import io.quarkus.security.Authenticated;
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
public class StaffResource {

  @Inject StaffService writeService;
  @Inject StaffReadService readService;
  @Inject PasswordService passwordService;
  @Inject AuthService authService;
  @Inject I18n i18n;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Retrieves a specific staff member by their linked account ID.
   *
   * <p>Available to any authenticated user so that students can see which staff members they will
   * work with.
   *
   * @param id the unique identifier (UUIDv7) of the staff member's account
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     StaffResponse}
   * @throws ResourceNotFoundException if the staff assignment is not found
   */
  @GET
  @Path("/{id}")
  @Authenticated
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    StaffView v = readService.getViewByAccountId(id);
    StaffResponse body = StaffPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a specific staff member by their registered email address.
   *
   * <p>Available to any authenticated user so that students can look up staff contacts by email.
   *
   * @param emailRaw the exact email string of the staff member
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     StaffResponse}
   * @throws AppValidationException if the provided email is malformed
   * @throws ResourceNotFoundException if no staff member is found with the given email
   */
  @GET
  @Path("/by-email/{email}")
  @Authenticated
  public Response getByEmail(@PathParam("email") @NotNull String emailRaw) {
    StaffView v = readService.getViewByEmail(emailRaw);
    StaffResponse out = StaffPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(out)).build();
  }

  /**
   * Retrieves the staff profile details of the currently authenticated user.
   *
   * <p>The account identifier is resolved from the JWT {@code accountId} claim via {@link
   * AuthService}, ensuring that staff members can only request their own staff profile. Restricted
   * to users with the STAFF role.
   *
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     StaffResponse}
   * @throws jakarta.ws.rs.NotAuthorizedException if the token is missing, invalid, or does not
   *     contain the required {@code accountId} claim
   */
  @GET
  @Path("/me")
  @Authenticated
  public Response getMe() {
    UUID accountId = authService.getCurrentAccountId();
    StaffView v = readService.getViewByAccountId(accountId);
    StaffResponse body = StaffPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a collection of staff members.
   *
   * <p>If the optional {@code q} parameter is provided, it executes a full-text search against the
   * personal names of the staff. If omitted, it returns an unfiltered list of all staff. Available
   * to any authenticated user so that students can see the staff members they will interact with.
   *
   * @param query the optional search query string
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     StaffResponse}
   */
  @GET
  @Authenticated
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
   * <p>Available to any authenticated user. Useful for administrative use cases and internal tools;
   * not a common path for students, but safe to expose read-only.
   *
   * @param cpfRaw the raw 11-digit numeric CPF string
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     StaffResponse}
   * @throws AppValidationException if the provided CPF is malformed
   */
  @GET
  @Path("/by-cpf/{cpf}")
  @Authenticated
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
   * <p>Available to any authenticated user so that students can see the staff of the entity they
   * will work with.
   *
   * @param entityId the unique identifier (UUID) of the partner entity
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     StaffResponse}
   */
  @GET
  @Path("/by-entity/{entityId}")
  @Authenticated
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
   * <p>Restricted to ADMIN and STAFF roles.
   */
  @POST
  @RolesAllowed({"ADMIN", "STAFF"})
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
   * <p>Restricted to ADMIN and STAFF roles.
   */
  @PUT
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "STAFF"})
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
   * <p>Restricted to ADMIN and STAFF roles.
   */
  @PATCH
  @Path("/{id}/deactivate")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response deactivate(@PathParam("id") @UuidV7 UUID id) {
    writeService.deactivate(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  /**
   * Permanently removes a staff member and their underlying account from the system.
   *
   * <p>Restricted to ADMIN role only.
   */
  @DELETE
  @Path("/{id}")
  @RolesAllowed("ADMIN")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
