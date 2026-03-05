package com.pug.partner.presenter;

import com.pug.identity.service.PasswordService;
import com.pug.identity.service.dtos.AccountCreateCommand;
import com.pug.identity.service.dtos.UserCreateCommand;
import com.pug.partner.domain.Staff;
import com.pug.partner.infra.read.dtos.StaffView;
import com.pug.partner.presenter.dtos.StaffCreateRequest;
import com.pug.partner.presenter.dtos.StaffResponse;
import com.pug.partner.presenter.mappers.StaffPresenter;
import com.pug.partner.service.StaffReadService;
import com.pug.partner.service.StaffService;
import com.pug.partner.service.dtos.StaffCreateCommand;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.utils.PresenterUtils;
import com.pug.shared.utils.StringUtils;
import com.pug.shared.validation.UuidV7;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST API Resource controller for managing Partner Staff privileges.
 *
 * <p>This class exposes endpoints to assign, retrieve, and revoke organizational roles for
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
  @Path("{id}")
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
  @Path("by-email/{email}")
  public Response getByEmail(@PathParam("email") @NotNull String emailRaw) {
    StaffView v = readService.getViewByEmail(emailRaw);
    StaffResponse out = StaffPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(out)).build();
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
    List<StaffView> views;

    if (StringUtils.isNotEmpty(query)) {
      views = readService.search(query);
    } else {
      views = readService.listViews();
    }

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
  @Path("by-cpf/{cpf}")
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
  @Path("by-entity/{entityId}")
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

    UserCreateCommand userCmd = new UserCreateCommand(req.cpfString(), req.name());
    AccountCreateCommand accountCmd =
        new AccountCreateCommand(req.emailString(), AccountType.PARTNER, hashedPassword, userCmd);
    StaffCreateCommand staffCmd = new StaffCreateCommand(req.entityId(), accountCmd);

    Staff staff = writeService.save(staffCmd);

    StaffView v = readService.getViewByAccountId(staff.getAccountId());
    StaffResponse out = StaffPresenter.toResponse(v, locale(), i18n);

    URI location = uri.getAbsolutePathBuilder().path(staff.getAccountId().toString()).build();

    return Response.created(location).entity(ApiEnvelope.created(out)).build();
  }

  /**
   * Permanently revokes staff privileges by deleting the assignment record and its associated
   * account.
   *
   * @param id the unique identifier (UUIDv7) of the staff's account
   * @return an HTTP 200 OK response with an empty data payload indicating successful deletion
   */
  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  /** Helper method to determine the preferred locale from the incoming request headers. */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
