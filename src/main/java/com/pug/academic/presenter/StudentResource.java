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

/** REST resource for managing students. */
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
   * Retrieves a student by their Account ID.
   *
   * @param id the UUID of the student's account.
   * @return the response containing the student details.
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    StudentView view = readService.getViewByAccountId(id);
    StudentResponse body = StudentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Lists students, optionally filtering by name search or course.
   *
   * @param q the search query (name).
   * @param courseId the course ID to filter by.
   * @return the response containing the list of students.
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
   * Retrieves a student by their academic registration.
   *
   * @param registration the academic registration string.
   * @return the response containing the student details.
   */
  @GET
  @Path("/by-registration/{registration}")
  public Response getByRegistration(@PathParam("registration") @NotNull String registration) {
    StudentView view = readService.getViewByAcademicRegistration(registration);
    StudentResponse body = StudentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a student by their email.
   *
   * @param email the email string.
   * @return the response containing the student details.
   */
  @GET
  @Path("/by-email/{email}")
  public Response getByEmail(@PathParam("email") @NotNull String email) {
    StudentView view = readService.getViewByEmail(email);
    StudentResponse body = StudentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a student by their CPF.
   *
   * @param cpf the CPF string.
   * @return the response containing the student details.
   */
  @GET
  @Path("/by-cpf/{cpf}")
  public Response getByCpf(@PathParam("cpf") @NotNull String cpf) {
    StudentView view = readService.getViewByCpf(cpf);
    StudentResponse body = StudentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Creates a new student.
   *
   * @param req the student creation request.
   * @return the response containing the created student.
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
   * Updates an existing student.
   *
   * @param id the UUID of the student's account.
   * @param req the student update request.
   * @return the response containing the updated student.
   */
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid StudentUpdateRequest req) {
    String passwordHash = passwordService.hash(req.password());

    UserUpdateCommand userCmd = new UserUpdateCommand(req.cpf(), req.name());
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
   * Deletes a student by their Account ID.
   *
   * @param id the UUID of the student's account.
   * @return the response indicating success.
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
