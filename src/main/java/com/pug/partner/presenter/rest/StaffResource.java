package com.pug.partner.presenter.rest;

import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.partner.domain.Staff;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.presenter.dtos.StaffAssignRequest;
import com.pug.partner.presenter.dtos.StaffCreateRequest;
import com.pug.partner.infra.read.dtos.StaffView;
import com.pug.partner.service.StaffReadService;
import com.pug.partner.service.StaffService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.presenter.dtos.BulkCreateResult;
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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
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

  @Context UriInfo uri;

  /**
   * Gets a staff member by user ID.
   *
   * @param userId the user ID of the staff member.
   * @return the staff member view.
   */
  @GET
  @Path("/{userId}")
  public Response get(@PathParam("userId") UUID userId) {
    Objects.requireNonNull(userId, "userId");
    StaffView v = read.getView(userId);
    if (v == null) {
      throw new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND);
    }
    return Response.ok(ApiEnvelope.ok(v)).build();
  }

  /**
   * Lists staff members, optionally filtered by entity ID.
   *
   * @param entityId the entity ID to filter by (optional).
   * @return the list of staff member views.
   */
  @GET
  public Response list(@QueryParam("entityId") UUID entityId) {
    List<StaffView> list = entityId == null ? read.listViews() : read.listViewsByEntityId(entityId);
    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /**
   * Creates a new staff member.
   *
   * @param req the staff creation request.
   * @return the created staff member view.
   */
  @POST
  public Response create(@Valid StaffCreateRequest req) {
    Objects.requireNonNull(req, "req");
    var staff =
        service.assign(
            new Cpf(req.cpf()), req.name(), new Email(req.email()), req.password(), req.entityId());
    return getResponse(staff);
  }

  /**
   * Assigns an existing user as a staff member to an entity.
   *
   * @param req the staff assignment request.
   * @return the assigned staff member view.
   */
  @POST
  @Path("/assign")
  public Response assign(@Valid StaffAssignRequest req) {
    Objects.requireNonNull(req, "req");
    var staff = service.assign(req.userId(), req.entityId());
    return getResponse(staff);
  }

  /**
   * Builds the response for a created or assigned staff member.
   *
   * @param staff the staff member.
   * @return the response containing the staff member view.
   */
  private Response getResponse(Staff staff) {
    StaffView v = read.getView(staff.getUserId());
    if (v == null) {
      throw new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND);
    }
    URI location = uri.getAbsolutePathBuilder().path(staff.getUserId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(v)).build();
  }

  /**
   * Deletes staff members by user IDs.
   *
   * @param req the request containing user IDs to delete.
   * @return the result of the deletion.
   */
  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    Objects.requireNonNull(req, "req");
    long deleted = service.deleteByUserIds(req.ids());
    return Response.ok(ApiEnvelope.ok(BulkCreateResult.sizeOnly((int) deleted))).build();
  }
}
