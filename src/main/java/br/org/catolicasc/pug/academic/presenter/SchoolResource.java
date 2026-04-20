package br.org.catolicasc.pug.academic.presenter;

import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.academic.presenter.dtos.SchoolCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.SchoolResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.SchoolUpdateRequest;
import br.org.catolicasc.pug.academic.presenter.mappers.SchoolPresenter;
import br.org.catolicasc.pug.academic.service.SchoolReadService;
import br.org.catolicasc.pug.academic.service.SchoolService;
import br.org.catolicasc.pug.academic.service.dtos.SchoolCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.SchoolUpdateCommand;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
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

/**
 * REST API resource controller for managing academic schools (or departments).
 *
 * <p>This class exposes endpoints to create, retrieve, update, and delete schools. It delegates
 * commands to the {@link SchoolService} (writes) and queries to the {@link SchoolReadService}
 * (reads), adhering to CQRS principles.
 */
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
   * Retrieves a specific school by its unique UUID identifier.
   *
   * @param id the unique identifier (UUIDv7) of the school
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     SchoolResponse}
   * @throws ResourceNotFoundException if the school is not found
   */
  @GET
  @Path("/{id}")
  @Authenticated
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    SchoolView view = readService.getViewById(id);
    SchoolResponse body = SchoolPresenter.toResponse(view, locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a collection of schools.
   *
   * <p>If the optional {@code q} parameter is provided, it executes a full-text search against the
   * schools' names. If omitted, it returns an unfiltered list of all schools.
   *
   * @param q the optional search query string
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     SchoolResponse}
   */
  @GET
  @Authenticated
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
   * Registers a new academic school within the platform.
   *
   * @param req the validated {@link SchoolCreateRequest} payload
   * @return an HTTP 201 Created response containing a {@code Location} header and the created
   *     {@link SchoolResponse}
   * @throws DuplicateResourceException if a school with the exact name
   *     already exists
   */
  @POST
  @RolesAllowed("ADMIN")
  public Response create(@Valid SchoolCreateRequest req) {
    SchoolCreateCommand cmd = SchoolPresenter.toCommand(req);
    School created = writeService.save(cmd);

    SchoolView view = readService.getViewById(created.getId());
    SchoolResponse body = SchoolPresenter.toResponse(view, locale());

    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Partially updates an existing school's details.
   *
   * @param id the unique identifier (UUIDv7) of the school to update
   * @param req the validated {@link SchoolUpdateRequest} containing the modified data
   * @return an HTTP 200 OK response containing the updated {@link SchoolResponse}
   */
  @PUT
  @Path("/{id}")
  @RolesAllowed("ADMIN")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid SchoolUpdateRequest req) {
    SchoolUpdateCommand cmd = SchoolPresenter.toCommand(req);
    writeService.update(id, cmd);

    SchoolView view = readService.getViewById(id);
    SchoolResponse body = SchoolPresenter.toResponse(view, locale());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Permanently removes an academic school from the system.
   *
   * @param id the unique identifier (UUIDv7) of the school to delete
   * @return an HTTP 200 OK response with an empty data payload indicating successful deletion
   */
  @DELETE
  @Path("/{id}")
  @RolesAllowed("ADMIN")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  /**
   * Helper method to determine the preferred locale from the incoming request headers.
   *
   * @return the resolved {@link Locale}
   */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
