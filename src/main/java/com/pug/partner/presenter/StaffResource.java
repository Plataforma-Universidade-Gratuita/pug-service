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
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DataIntegrityException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.dtos.BulkCreateResult;
import com.pug.shared.presenter.dtos.DeleteResult;
import com.pug.shared.presenter.dtos.UuidsRequest;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.utils.PresenterUtils;
import com.pug.shared.utils.StringUtils;
import com.pug.shared.validation.UuidV7;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/** REST resource for managing partner staff. */
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
   * Picks the best matching locale from the request headers.
   *
   * @return the selected Locale
   */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }

  /**
   * Retrieves a staff member by their unique identifier.
   *
   * @param id the UUID of the staff member
   * @return a Response containing the staff member details
   */
  @GET
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    StaffView v = readService.getViewById(id);
    StaffResponse body = StaffPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Lists all staff members.
   *
   * @return a Response containing a list of all staff members
   */
  @GET
  public Response list() {
    List<StaffResponse> list =
        readService.listViews().stream()
            .map(v -> StaffPresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());
    return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(list))).build();
  }

  /**
   * Retrieves staff members by their CPF.
   *
   * @param cpfRaw the CPF of the staff members
   * @return a Response containing the staff members details
   * @throws AppValidationException if the provided CPF is malformed.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no staff member is found.
   */
  @GET
  @Path("by-cpf/{cpf}")
  public Response getByCpf(@PathParam("cpf") String cpfRaw) {
    List<StaffResponse> list =
        readService.listViewsByCpf(cpfRaw).stream()
            .map(v -> StaffPresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());
    return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(list))).build();
  }

  /**
   * Retrieves a staff member by their email address.
   *
   * @param rawEmail the email address of the staff member
   * @return a Response containing the staff member details
   * @throws AppValidationException if the provided email is malformed.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no staff member is found.
   */
  @GET
  @Path("by-email")
  public Response getByEmail(@QueryParam("email") String rawEmail) {
    if (StringUtils.isEmpty(rawEmail)) {
      return list();
    }
    StaffView v = readService.getViewByEmail(rawEmail);
    StaffResponse out = StaffPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(out)).build();
  }

  /**
   * Searches for staff members by name.
   *
   * @param query the name query string
   * @return a Response containing a list of matching staff members
   */
  @GET
  @Path("by-name")
  public Response listByName(@QueryParam("q") String query) {
    if (StringUtils.isEmpty(query)) {
      return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(List.of()))).build();
    }

    List<StaffResponse> list =
        readService.search(query).stream()
            .map(v -> StaffPresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(list))).build();
  }

  /**
   * Lists staff members associated with a specific entity.
   *
   * @param entityId the UUID of the entity
   * @return a Response containing a list of staff members for the entity
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the entity is not found
   *     (propagated from service).
   */
  @GET
  @Path("by-entity/{entityId}")
  public Response listByEntity(@PathParam("entityId") @UuidV7 UUID entityId) {
    List<StaffResponse> list =
        readService.listViewsByEntityId(entityId).stream()
            .map(v -> StaffPresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(list))).build();
  }

  /**
   * Creates a new staff member.
   *
   * @param req the request containing staff member details.
   * @return a Response containing the created staff member details.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a staff member with the same
   *     account ID already exists.
   * @throws AppValidationException if input validation fails (e.g., blank/invalid CPF, email,
   *     etc.).
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the associated entity is not
   *     found.
   */
  @POST
  public Response create(@Valid StaffCreateRequest req) {
    String hashedPassword = passwordService.hash(req.password());

    UserCreateCommand userCmd = new UserCreateCommand(req.cpfString(), req.name());
    AccountCreateCommand accountCmd =
        new AccountCreateCommand(req.emailString(), AccountType.PARTNER, hashedPassword, userCmd);
    StaffCreateCommand staffCmd = new StaffCreateCommand(req.entityId(), accountCmd);

    Staff staff = writeService.save(staffCmd);

    StaffView v = readService.getViewById(staff.getAccountId());
    StaffResponse out = StaffPresenter.toResponse(v, locale(), i18n);

    URI location = uri.getAbsolutePathBuilder().path(staff.getAccountId().toString()).build();

    return Response.created(location).entity(ApiEnvelope.created(out)).build();
  }

  /**
   * Creates multiple staff members in bulk.
   *
   * @param reqs the list of requests containing staff member details.
   * @return a Response containing the result of the bulk creation.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if any staff member with the same
   *     account ID already exists.
   * @throws AppValidationException if input validation fails for any staff in the bulk.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if any associated entity is not
   *     found.
   */
  @POST
  @Path("bulk")
  public Response createBulk(@Valid List<StaffCreateRequest> reqs) {
    List<StaffCreateCommand> cmds =
        reqs.stream()
            .map(
                req -> {
                  String hashedPassword = passwordService.hash(req.password());

                  UserCreateCommand userCmd = new UserCreateCommand(req.cpfString(), req.name());

                  AccountCreateCommand accountCmd =
                      new AccountCreateCommand(
                          req.emailString(), AccountType.PARTNER, hashedPassword, userCmd);

                  return new StaffCreateCommand(req.entityId(), accountCmd);
                })
            .toList();

    List<Staff> staffList = writeService.saveAll(cmds);

    return Response.ok(ApiEnvelope.ok(BulkCreateResult.sizeOnly(staffList.size()))).build();
  }

  /**
   * Updates an existing staff member.
   *
   * @param id the UUID of the staff member's account to update.
   * @param req the request containing updated staff member details.
   * @return a Response containing the updated staff member details.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the staff member does not exist.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if an admin with the updated
   *     email/CPF already exists.
   * @throws AppValidationException if input validation fails.
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

    StaffView v = readService.getViewById(updatedStaff.getAccountId());
    StaffResponse out = StaffPresenter.toResponse(v, locale(), i18n);

    return Response.ok(ApiEnvelope.ok(out)).build();
  }

  /**
   * Deletes staff members by their unique identifiers.
   *
   * @param req the request containing the list of staff member IDs to delete.
   * @return a Response containing the result of the deletion.
   * @throws DataIntegrityException if any associated account is still
   *     referenced.
   */
  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    Map<DeleteKeys, Long> result = writeService.deleteAll(req.ids());
    return Response.ok(ApiEnvelope.ok(new DeleteResult(result))).build();
  }
}
