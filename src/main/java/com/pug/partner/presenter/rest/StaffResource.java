package com.pug.partner.presenter.rest;

import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.partner.domain.Staff;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.infra.read.dtos.StaffView;
import com.pug.partner.presenter.dtos.StaffCreateRequest;
import com.pug.partner.presenter.dtos.StaffResponse;
import com.pug.partner.presenter.mappers.StaffPresenter;
import com.pug.partner.service.StaffReadService;
import com.pug.partner.service.StaffService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.dtos.DeleteResult;
import com.pug.shared.presenter.dtos.UuidsRequest;
import com.pug.shared.presenter.rest.ApiEnvelope;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** REST resource for partner staff. */
@ApplicationScoped
@Path("/partners/staff")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StaffResource {

  @Inject StaffService service;
  @Inject StaffReadService read;
  @Inject I18n i18n;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Retrieves a staff member by user ID.
   *
   * @param userId the user ID of the staff member
   * @return the Response containing the StaffResponse
   * @throws ResourceNotFoundException if the staff member is not found
   */
  @GET
  @Path("/{userId}")
  public Response get(@PathParam("userId") UUID userId) {
    Objects.requireNonNull(userId, "userId");
    StaffView v = read.getView(userId);
    if (v == null) {
      throw new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND);
    }
    StaffResponse out = StaffPresenter.toResponse(v, resolveLocale(), i18n);
    return Response.ok(ApiEnvelope.ok(out)).build();
  }

  /**
   * Lists staff members, optionally filtered by entity ID.
   *
   * @param entityId the optional entity ID to filter staff members
   * @return the Response containing the list of StaffResponse
   */
  @GET
  public Response list(@QueryParam("entityId") UUID entityId) {
    List<StaffView> list = entityId == null ? read.listViews() : read.listViewsByEntityId(entityId);
    Locale locale = resolveLocale();
    List<StaffResponse> out =
        list.stream().map(v -> StaffPresenter.toResponse(v, locale, i18n)).toList();
    return Response.ok(ApiEnvelope.ok(out)).build();
  }

  /**
   * Creates a new staff member.
   *
   * @param req the StaffCreateRequest containing staff details
   * @return the Response containing the created StaffResponse
   */
  @POST
  public Response create(@Valid StaffCreateRequest req) {
    Objects.requireNonNull(req, "req");
    var staff =
        service.assign(
            new Cpf(req.cpf()), req.name(), new Email(req.email()), req.password(), req.entityId());
    return createdResponse(staff);
  }

  /**
   * Deletes staff members by user IDs.
   *
   * @param req the UuidsRequest containing user IDs to delete
   * @return the Response containing the DeleteResult
   */
  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    Objects.requireNonNull(req, "req");
    Map<String, Long> deleted = service.deleteByUserIds(req.ids());
    return Response.ok(ApiEnvelope.ok(new DeleteResult(deleted))).build();
  }

  /**
   * Creates a Response for a created Staff resource.
   *
   * @param staff the created Staff
   * @return the Response
   */
  private Response createdResponse(Staff staff) {
    StaffView v = read.getView(staff.getUserId());
    if (v == null) {
      throw new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND);
    }
    StaffResponse out = StaffPresenter.toResponse(v, resolveLocale(), i18n);
    URI location = uri.getAbsolutePathBuilder().path(staff.getUserId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(out)).build();
  }

  /**
   * Resolves the locale from the Accept-Language header.
   *
   * @return the resolved Locale.
   */
  private Locale resolveLocale() {
    var acceptable = headers.getAcceptableLanguages();
    return acceptable == null || acceptable.isEmpty() ? Locale.getDefault() : acceptable.getFirst();
  }
}
