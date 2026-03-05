package com.pug.academic.presenter;

import com.pug.academic.domain.Student;
import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.academic.presenter.dtos.StudentCreateRequest;
import com.pug.academic.presenter.dtos.StudentResponse;
import com.pug.academic.presenter.dtos.StudentUpdateRequest;
import com.pug.academic.presenter.mappers.StudentPresenter;
import com.pug.academic.service.StudentReadService;
import com.pug.academic.service.StudentService;
import com.pug.academic.service.dtos.StudentCreateCommand;
import com.pug.academic.service.dtos.StudentUpdateCommand;
import com.pug.identity.service.PasswordService;
import com.pug.identity.service.dtos.AccountCreateCommand;
import com.pug.identity.service.dtos.AccountUpdateCommand;
import com.pug.identity.service.dtos.UserCreateCommand;
import com.pug.identity.service.dtos.UserUpdateCommand;
import com.pug.shared.domain.enums.AccountType;
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
 * REST API Resource controller for managing Student enrollments.
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
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the student is not found
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    StudentView view = readService.getViewByAccountId(id);
    StudentResponse body = StudentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a specific student by their exact academic registration number.
   *
   * @param registration the academic registration string
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     StudentResponse}
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the student is not found
   */
  @GET
  @Path("/by-registration/{registration}")
  public Response getByRegistration(@PathParam("registration") @NotNull String registration) {
    StudentView view = readService.getViewByAcademicRegistration(registration);
    StudentResponse body = StudentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a specific student by their registered email address.
   *
   * @param email the exact email string of the student
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     StudentResponse}
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the student is not found
   */
  @GET
  @Path("/by-email/{email}")
  public Response getByEmail(@PathParam("email") @NotNull String email) {
    StudentView view = readService.getViewByEmail(email);
    StudentResponse body = StudentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a specific student by their exact CPF.
   *
   * @param cpf the raw 11-digit numeric CPF string
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     StudentResponse}
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the student is not found
   */
  @GET
  @Path("/by-cpf/{cpf}")
  public Response getByCpf(@PathParam("cpf") @NotNull String cpf) {
    StudentView view = readService.getViewByCpf(cpf);
    StudentResponse body = StudentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a collection of students.
   *
   * <p>If the optional {@code q} parameter is provided, it executes a full-text search against the
   * students' personal names. If the {@code courseId} is provided, it filters the students by their
   * enrolled course. If both are omitted, it returns all students.
   *
   * @param q the optional search query string
   * @param courseId the optional course identifier to filter by
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     StudentResponse}
   */
  @GET
  public Response list(@QueryParam("q") String q, @QueryParam("courseId") @UuidV7 UUID courseId) {

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
   * @throws com.pug.shared.exceptions.DuplicateResourceException if the academic registration or
   *     email already exists
   */
  @POST
  public Response create(@Valid StudentCreateRequest req) {
    String hashedPassword = passwordService.hash(req.password());

    UserCreateCommand userCmd = new UserCreateCommand(req.cpf(), req.name());
    AccountCreateCommand accountCmd =
        new AccountCreateCommand(req.email(), AccountType.STUDENT, hashedPassword, userCmd);
    StudentCreateCommand studentCmd =
        new StudentCreateCommand(
            accountCmd,
            req.academicRegistration(),
            req.campus(),
            req.courseId(),
            req.requiredHours(),
            req.startDate(),
            req.dueDate());

    Student created = writeService.save(studentCmd);
    StudentView view = readService.getViewByAccountId(created.getAccountId());
    StudentResponse body = StudentPresenter.toResponse(view, locale(), i18n);

    URI location = uri.getAbsolutePathBuilder().path(created.getAccountId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
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
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid StudentUpdateRequest req) {
    // Only hash the password if a new one was provided
    String passwordHash = null;
    if (StringUtils.isNotEmpty(req.password())) {
      passwordHash = passwordService.hash(req.password());
    }

    UserUpdateCommand userCmd = new UserUpdateCommand(req.name());
    AccountUpdateCommand accountCmd = new AccountUpdateCommand(req.email(), passwordHash, userCmd);
    StudentUpdateCommand studentCmd =
        new StudentUpdateCommand(
            accountCmd,
            req.academicRegistration(),
            req.campus(),
            req.courseId(),
            req.requiredHours(),
            req.startDate(),
            req.dueDate());

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
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  /** Helper method to determine the preferred locale from the incoming request headers. */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
