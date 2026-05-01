package br.org.catolicasc.pug.academic.presenter;

import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.academic.infra.read.dtos.StudentView;
import br.org.catolicasc.pug.academic.presenter.dtos.StudentCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.StudentResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.StudentUpdateRequest;
import br.org.catolicasc.pug.academic.presenter.mappers.StudentPresenter;
import br.org.catolicasc.pug.academic.service.StudentReadService;
import br.org.catolicasc.pug.academic.service.StudentService;
import br.org.catolicasc.pug.academic.service.dtos.StudentCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.StudentUpdateCommand;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.identity.service.PasswordService;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
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
 * REST API resource controller for managing student enrollments.
 *
 * <p>This class exposes endpoints to enroll, retrieve, update, and remove students. It delegates
 * commands to the {@link StudentService} (writes) and queries to the {@link StudentReadService}
 * (reads), adhering to CQRS principles.
 */
@ApplicationScoped
@Path("/academic/students")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentResource {

  @Inject AuthService authService;
  @Inject StudentService writeService;
  @Inject StudentReadService readService;
  @Inject PasswordService passwordService;
  @Inject I18n i18n;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Retrieves a specific student by their linked account UUID.
   *
   * @param id the unique identifier (UUIDv7) of the student's account
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     StudentResponse}
   * @throws ResourceNotFoundException if the student is not found
   */
  @GET
  @Path("/{id}")
  @Authenticated
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    StudentView view = readService.getViewByAccountId(id);
    StudentResponse body = StudentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves the academic enrollment details of the currently authenticated student.
   *
   * <p>The account identifier is resolved from the JWT {@code accountId} claim via {@link
   * AuthService}, ensuring that callers can only access their own academic record. This endpoint is
   * restricted to users with the STUDENT role.
   *
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     StudentResponse}
   * @throws jakarta.ws.rs.NotAuthorizedException if the token is missing, invalid, or does not
   *     contain the required {@code accountId} claim
   */
  @GET
  @Path("/me")
  @Authenticated
  public Response getMe() {
    UUID accountId = authService.getCurrentAccountId();
    StudentView view = readService.getViewByAccountId(accountId);
    StudentResponse body = StudentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves students, optionally filtered by query parameters.
   *
   * <p>When {@code cpf}, {@code email}, or {@code registration} is provided, this endpoint returns
   * the single student linked to that identifier. Otherwise, it filters by {@code courseId}, falls
   * back to full-text search with {@code q}, or lists all students when no filters are supplied.
   *
   * @param q the optional search query string
   * @param courseId the optional course identifier to filter by
   * @param cpf the optional CPF used to retrieve a single student
   * @param email the optional email used to retrieve a single student
   * @param registration the optional academic registration used to retrieve a single student
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with either a single {@link
   *     StudentResponse} or a list of {@link StudentResponse}
   */
  @GET
  @Authenticated
  public Response list(
      @QueryParam("q") String q,
      @QueryParam("courseId") @UuidV7 UUID courseId,
      @QueryParam("cpf") String cpf,
      @QueryParam("email") String email,
      @QueryParam("registration") String registration) {

    if (StringUtils.isNotEmpty(cpf)) {
      StudentView view = readService.getViewByCpf(cpf);
      StudentResponse body = StudentPresenter.toResponse(view, locale(), i18n);
      return Response.ok(ApiEnvelope.ok(body)).build();
    }

    if (StringUtils.isNotEmpty(email)) {
      StudentView view = readService.getViewByEmail(email);
      StudentResponse body = StudentPresenter.toResponse(view, locale(), i18n);
      return Response.ok(ApiEnvelope.ok(body)).build();
    }

    if (StringUtils.isNotEmpty(registration)) {
      StudentView view = readService.getViewByAcademicRegistration(registration);
      StudentResponse body = StudentPresenter.toResponse(view, locale(), i18n);
      return Response.ok(ApiEnvelope.ok(body)).build();
    }

    List<StudentView> views;

    if (courseId != null) {
      views = readService.listViewsByCourseId(courseId);
    } else if (StringUtils.isNotEmpty(q)) {
      views = readService.searchByName(q);
    } else {
      views = readService.listViews();
    }

    List<StudentResponse> body =
        views.stream()
            .map(v -> StudentPresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Registers a new student within the platform.
   *
   * <p>This endpoint processes an aggregated payload, automatically handling the provisioning of
   * the underlying user and authentication account within a single transaction.
   *
   * @param req the validated {@link StudentCreateRequest} containing identity and enrollment
   *     details
   * @return an HTTP 201 Created response containing a {@code Location} header and the created
   *     {@link StudentResponse}
   * @throws DuplicateResourceException if the academic registration or email already exists
   */
  @POST
  @RolesAllowed("ADMIN")
  public Response create(@Valid StudentCreateRequest req) {
    String hashedPassword = passwordService.hash(req.password());
    StudentCreateCommand studentCmd = StudentPresenter.toCommand(req, hashedPassword);

    Student created = writeService.save(studentCmd);
    StudentView view = readService.getViewByAccountId(created.getAccountId());
    StudentResponse body = StudentPresenter.toResponse(view, locale(), i18n);

    URI location = uri.getAbsolutePathBuilder().path(created.getAccountId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Registers a batch of new students within the platform.
   *
   * <p>This endpoint processes an aggregated payload of multiple students, automatically handling
   * the provisioning of the underlying users and authentication accounts within a single
   * transaction.
   *
   * @param reqs a {@link List} of validated {@link StudentCreateRequest} containing identity and
   *     enrollment details
   * @return an HTTP 201 Created response containing the created {@link List} of {@link
   *     StudentResponse}
   * @throws DuplicateResourceException if any academic registration or email already exists
   */
  @POST
  @Path("/bulk")
  @RolesAllowed("ADMIN")
  public Response createInBulk(@Valid @NotNull List<StudentCreateRequest> reqs) {
    List<StudentCreateCommand> cmds =
        reqs.stream()
            .map(
                req -> {
                  String hashedPassword = passwordService.hash(req.password());
                  return StudentPresenter.toCommand(req, hashedPassword);
                })
            .toList();

    List<Student> createdStudents = writeService.saveInBulk(cmds);

    List<UUID> accountIds = createdStudents.stream().map(Student::getAccountId).toList();
    List<StudentView> views = readService.listViewsByAccountIds(accountIds);

    List<StudentResponse> body =
        views.stream().map(view -> StudentPresenter.toResponse(view, locale(), i18n)).toList();

    return Response.status(Response.Status.CREATED).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Partially updates an existing student's enrollment details.
   *
   * @param id the unique identifier (UUIDv7) of the student's account
   * @param req the validated {@link StudentUpdateRequest} containing the modified data
   * @return an HTTP 200 OK response containing the updated {@link StudentResponse}
   */
  @PUT
  @Path("/{id}")
  @RolesAllowed("ADMIN")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid StudentUpdateRequest req) {
    String passwordHash = null;
    if (StringUtils.isNotEmpty(req.password())) {
      passwordHash = passwordService.hash(req.password());
    }
    StudentUpdateCommand studentCmd = StudentPresenter.toCommand(req, passwordHash);

    writeService.update(id, studentCmd);
    StudentView view = readService.getViewByAccountId(id);
    StudentResponse body = StudentPresenter.toResponse(view, locale(), i18n);

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Permanently removes a student enrollment from the system and revokes their account access.
   *
   * @param id the unique identifier (UUIDv7) of the student's account to delete
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
