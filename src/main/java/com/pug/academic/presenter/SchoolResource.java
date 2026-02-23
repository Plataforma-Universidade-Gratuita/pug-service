package com.pug.academic.presenter;

import com.pug.academic.domain.School;
import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.academic.presenter.dtos.SchoolCreateRequest;
import com.pug.academic.presenter.dtos.SchoolResponse;
import com.pug.academic.presenter.dtos.SchoolUpdateRequest;
import com.pug.academic.presenter.mappers.SchoolPresenter;
import com.pug.academic.service.SchoolReadService;
import com.pug.academic.service.SchoolService;
import com.pug.academic.service.dtos.SchoolCreateCommand;
import com.pug.academic.service.dtos.SchoolUpdateCommand;
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
import java.util.UUID;
import java.util.stream.Collectors;

/** REST resource for managing schools. */
@ApplicationScoped
@Path("/academic/schools")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SchoolResource {

  @Inject SchoolService writeService;
  @Inject SchoolReadService readService;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Retrieves a school by its ID.
   *
   * @param id the ID of the school
   * @return the response containing the school
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    SchoolView view = readService.getViewById(id);
    SchoolResponse body = SchoolPresenter.toResponse(view, locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Lists all schools or searches schools by name.
   *
   * @param q the optional search query
   * @return the response containing the list of schools
   */
  @GET
  public Response list(@QueryParam("q") String q) {
    List<SchoolView> views;

    if (StringUtils.isNotEmpty(q)) {
      views = readService.searchByName(q);
    } else {
      views = readService.listAll();
    }

    List<SchoolResponse> body =
        views.stream()
            .map(v -> SchoolPresenter.toResponse(v, locale()))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Creates a new school.
   *
   * @param req the school creation request
   * @return the response containing the created school
   */
  @POST
  public Response create(@Valid SchoolCreateRequest req) {
    SchoolCreateCommand cmd = new SchoolCreateCommand(req.name());
    School created = writeService.save(cmd);

    SchoolView view = readService.getViewById(created.getId());
    SchoolResponse body = SchoolPresenter.toResponse(view, locale());

    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Updates an existing school.
   *
   * @param id the ID of the school to update
   * @param req the school update request
   * @return the response containing the updated school
   */
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid SchoolUpdateRequest req) {
    SchoolUpdateCommand cmd = new SchoolUpdateCommand(req.name());
    writeService.update(id, cmd);

    SchoolView view = readService.getViewById(id);
    SchoolResponse body = SchoolPresenter.toResponse(view, locale());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Deletes a school by its ID.
   *
   * @param id the ID of the school to delete
   * @return the response containing the result of the deletion
   */
  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  /** Picks the best locale from the Accept-Language header. */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
