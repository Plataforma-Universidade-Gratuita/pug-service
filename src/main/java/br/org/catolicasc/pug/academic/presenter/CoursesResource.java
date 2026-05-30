package br.org.catolicasc.pug.academic.presenter;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.infra.read.dtos.CourseView;
import br.org.catolicasc.pug.academic.presenter.dtos.courses.CourseComplexSearchRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.courses.CourseCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.courses.CourseResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.courses.CourseUpdateRequest;
import br.org.catolicasc.pug.academic.presenter.mappers.CoursePresenter;
import br.org.catolicasc.pug.academic.service.CoursesReadService;
import br.org.catolicasc.pug.academic.service.CoursesService;
import br.org.catolicasc.pug.academic.service.dtos.courses.CourseComplexSearchCriteria;
import br.org.catolicasc.pug.academic.service.dtos.courses.CourseCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.courses.CourseUpdateCommand;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.presenter.dtos.PageResponse;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
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

/**
 * REST API resource controller for managing academic courses.
 *
 * <p>This class exposes endpoints to create, retrieve, update, delete, and search courses. It
 * delegates commands to the {@link CoursesService} and queries to the {@link CoursesReadService},
 * strictly adhering to CQRS principles.
 */
@ApplicationScoped
@Path("/v1/academic/courses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CoursesResource {

  @Inject CoursesService writeService;
  @Inject CoursesReadService readService;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Retrieves a specific course by its unique UUID identifier.
   *
   * @param id the unique identifier (UUIDv7) of the course
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     CourseResponse}
   * @throws ResourceNotFoundException if the course is not found
   */
  @GET
  @Path("/{id}")
  @Authenticated
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    CourseView view = readService.getViewById(id);
    CourseResponse body = CoursePresenter.toResponse(view, locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves courses, optionally restricted to a provided collection of identifiers.
   *
   * <p>When one or more {@code ids} query parameters are present, this endpoint returns only the
   * matching courses. Otherwise, it returns the complete course list ordered according to the
   * underlying query implementation.
   *
   * @param ids the optional course identifiers used to restrict the returned collection
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     CourseResponse}
   */
  @GET
  @Authenticated
  public Response list(@QueryParam("ids") List<UUID> ids) {
    List<CourseView> views =
        CollectionUtils.isEmpty(ids) ? readService.listViews() : readService.listViewsByIds(ids);
    List<CourseResponse> body =
        views.stream().map(v -> CoursePresenter.toResponse(v, locale())).toList();

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Executes paginated course search using the academic complex-search contract.
   *
   * @param page the zero-based page index
   * @param size the requested page size; {@code 1} returns the full result set in a single page
   * @param request the optional complex-search filters
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the paginated search
   *     result
   */
  @POST
  @Path("search")
  @Authenticated
  public Response search(
      @QueryParam("page") @DefaultValue("0") @Min(0) int page,
      @QueryParam("size") @DefaultValue("25") @Min(1) int size,
      @Valid CourseComplexSearchRequest request) {
    CourseComplexSearchCriteria criteria =
        request == null
            ? new CourseComplexSearchCriteria(null, null)
            : new CourseComplexSearchCriteria(request.name(), request.areaOfExpertiseIds());
    var result = readService.search(new PageQuery(page, size), criteria);
    var responseBody =
        new PageResponse<>(
            result.content().stream()
                .map(view -> CoursePresenter.toWithAuditInfoComplexSearchResponse(view, locale()))
                .toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages());

    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }

  /**
   * Registers a new academic course within the platform.
   *
   * @param req the validated {@link CourseCreateRequest} payload
   * @return an HTTP 201 Created response containing a {@code Location} header and the created
   *     {@link CourseResponse}
   * @throws DuplicateResourceException if a course with the same name already exists
   */
  @POST
  @RolesAllowed("ADMIN")
  public Response create(@Valid CourseCreateRequest req) {
    CourseCreateCommand cmd = CoursePresenter.toCommand(req);
    Course created = writeService.save(cmd);

    CourseView view = readService.getViewById(created.getId());
    CourseResponse body = CoursePresenter.toResponse(view, locale());

    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Updates an existing course's details.
   *
   * @param id the unique identifier (UUIDv7) of the course to update
   * @param req the validated {@link CourseUpdateRequest} containing the modified data
   * @return an HTTP 200 OK response containing the updated {@link CourseResponse}
   */
  @PUT
  @Path("/{id}")
  @RolesAllowed("ADMIN")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid CourseUpdateRequest req) {
    CourseUpdateCommand cmd = CoursePresenter.toCommand(req);
    writeService.update(id, cmd);

    CourseView view = readService.getViewById(id);
    CourseResponse body = CoursePresenter.toResponse(view, locale());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Permanently removes an academic course from the system.
   *
   * @param id the unique identifier (UUIDv7) of the course to delete
   * @return an HTTP 204 No Content response when deletion succeeds
   */
  @DELETE
  @Path("/{id}")
  @RolesAllowed("ADMIN")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.noContent().build();
  }

  /** Helper method to determine the preferred locale from the incoming request headers. */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
