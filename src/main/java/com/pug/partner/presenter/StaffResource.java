package com.pug.partner.presenter;

import com.pug.identity.service.PasswordService;
import com.pug.identity.service.dtos.AccountCreateCommand;
import com.pug.identity.service.dtos.AccountUpdateCommand;
import com.pug.identity.service.dtos.UserCreateCommand;
import com.pug.identity.service.dtos.UserUpdateCommand;
import com.pug.partner.domain.Staff;
import com.pug.partner.infra.read.dtos.StaffView;
import com.pug.partner.presenter.dtos.StaffCreateRequest;
import com.pug.partner.presenter.dtos.StaffResponse;
import com.pug.partner.presenter.dtos.StaffUpdateRequest;
import com.pug.partner.presenter.mappers.StaffPresenter;
import com.pug.partner.service.StaffReadService;
import com.pug.partner.service.StaffService;
import com.pug.partner.service.dtos.StaffCreateCommand;
import com.pug.partner.service.dtos.StaffUpdateCommand;
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
 * REST resource for managing partner staff.
 */
@ApplicationScoped
@Path("/partners/staff")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StaffResource {

  @Inject
  StaffService writeService;
  @Inject
  StaffReadService readService;
  @Inject
  PasswordService passwordService;
  @Inject
  I18n i18n;

  @Context
  UriInfo uri;
  @Context
  HttpHeaders headers;

  /**
   * Retrieves a staff member by their unique identifier.
   *
   * @param id the UUID of the staff member
   * @return a Response containing the staff member details
   */
  @GET
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    StaffView v = readService.getViewByAccountId(id);
    StaffResponse body = StaffPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Lists staff members.
   * <p>
   * If the 'q' query parameter is provided, performs a search by name.
   * Otherwise, returns all staff.
   * </p>
   *
   * @param query optional name query to search for.
   * @return a Response containing a list of staff members
   */
  @GET
  public Response list(@QueryParam("q") String query) {
    List<StaffView> views;

    if (StringUtils.isNotEmpty(query)) {
      views = readService.search(query);
    } else {
      views = readService.listViews();
    }

    List<StaffResponse> list = views.stream()
            .map(v -> StaffPresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /**
   * Retrieves staff members by their CPF.
   *
   * @param cpfRaw the CPF of the staff members
   * @return a Response containing the staff members details
   * @throws AppValidationException if the provided CPF is malformed.
   */
  @GET
  @Path("by-cpf/{cpf}")
  public Response getByCpf(@PathParam("cpf") String cpfRaw) {
    List<StaffResponse> list =
            readService.listViewsByCpf(cpfRaw).stream()
                    .map(v -> StaffPresenter.toResponse(v, locale(), i18n))
                    .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /**
   * Retrieves a staff member by their email address.
   *
   * @param emailRaw the email address of the staff member
   * @return a Response containing the staff member details
   * @throws AppValidationException    if the provided email is malformed.
   * @throws ResourceNotFoundException if no staff member is found.
   */
  @GET
  @Path("by-email/{email}")
  public Response getByEmail(@PathParam("email") @NotNull String emailRaw) {
    StaffView v = readService.getViewByEmail(emailRaw);
    StaffResponse out = StaffPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(out)).build();
  }

  /**
   * Lists staff members associated with a specific entity.
   *
   * @param entityId the UUID of the entity
   * @return a Response containing a list of staff members for the entity
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
   * Creates a new staff member.
   *
   * @param req the request containing staff member details.
   * @return a Response containing the created staff member details.
   * @throws DuplicateResourceException if a staff member with the same account ID already exists.
   * @throws AppValidationException     if input validation fails.
   * @throws ResourceNotFoundException  if the associated entity is not found.
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
   * Updates an existing staff member.
   *
   * @param id  the UUID of the staff member's account to update.
   * @param req the request containing updated staff member details.
   * @return a Response containing the updated staff member details.
   * @throws ResourceNotFoundException  if the staff member does not exist.
   * @throws DuplicateResourceException if updated details conflict with existing records.
   * @throws AppValidationException     if input validation fails.
   */
  @PUT
  @Path("{id}")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid StaffUpdateRequest req) {
    String hashedPassword = req.password() != null ? passwordService.hash(req.password()) : null;

    var userCmd = new UserUpdateCommand(req.cpfString(), req.name());
    AccountUpdateCommand accountCmd =
            new AccountUpdateCommand(req.emailString(), hashedPassword, userCmd);
    StaffUpdateCommand staffCmd = new StaffUpdateCommand(req.entityId(), accountCmd);

    Staff updatedStaff = writeService.update(id, staffCmd);

    StaffView v = readService.getViewByAccountId(updatedStaff.getAccountId());
    StaffResponse out = StaffPresenter.toResponse(v, locale(), i18n);

    return Response.ok(ApiEnvelope.ok(out)).build();
  }

  /**
   * Deletes a staff member by ID.
   *
   * @param id the UUID of the staff member (account ID) to delete.
   * @return 200 OK with empty data (idempotent).
   */
  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  /**
   * Picks the best locale from the request headers.
   */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}