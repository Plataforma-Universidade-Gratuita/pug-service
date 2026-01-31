package com.pug.academic.presenter;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.enums.Campi;
import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.academic.presenter.dtos.StudentCreateRequest;
import com.pug.academic.presenter.dtos.StudentResponse;
import com.pug.academic.presenter.dtos.StudentUpdateRequest;
import com.pug.academic.presenter.mappers.StudentPresenter;
import com.pug.academic.service.IStudentReadService;
import com.pug.academic.service.IStudentService;
import com.pug.academic.service.dtos.StudentCreateCommand;
import com.pug.academic.service.dtos.StudentUpdateCommand;
import com.pug.identity.service.dtos.AccountCreateCommand;
import com.pug.identity.service.dtos.AccountUpdateCommand;
import com.pug.identity.service.dtos.UserCreateCommand;
import com.pug.identity.service.dtos.UserUpdateCommand;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.ResourceNotFoundException;
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
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/** REST resource for managing students in the academic system. */
@ApplicationScoped
@Path("/academic/students")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentResource {

  @Inject IStudentService writeService;
  @Inject IStudentReadService readService;
  @Inject I18n i18n;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Picks the best locale from the Accept-Language header.
   *
   * @return the selected Locale.
   */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }

  /**
   * Creates a new student in the academic system.
   *
   * @param req the request containing student details
   * @return a Response with the created student details
   */
  @POST
  public Response create(@Valid StudentCreateRequest req) {
    UserCreateCommand userCmd = new UserCreateCommand(req.cpf(), req.name());
    AccountCreateCommand accountCmd =
        new AccountCreateCommand(req.email(), AccountType.STUDENT, req.password(), userCmd);
    Campi campusEnum = Campi.valueOf(req.campus().trim().toUpperCase(Locale.ROOT));

    StudentCreateCommand studentCmd =
        new StudentCreateCommand(
            accountCmd,
            req.academicRegistration(),
            campusEnum,
            req.courseId(),
            req.requiredHours(),
            BigDecimal.ZERO,
            req.startDate(),
            req.dueDate());

    Student created = writeService.save(studentCmd);

    StudentView view = readService.getView(created.getAccountId());
    StudentResponse body = StudentPresenter.toResponse(view, locale(), i18n);
    URI location = uri.getAbsolutePathBuilder().path(created.getAccountId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Creates multiple students in bulk.
   *
   * @param reqs the list of StudentCreateRequest DTOs for bulk creation
   * @return a Response with the count of created students
   */
  @POST
  @Path("/bulk")
  public Response createBulk(@Valid List<StudentCreateRequest> reqs) {
    List<StudentCreateCommand> studentCommands =
        reqs.stream()
            .map(
                req -> {
                  UserCreateCommand userCmd = new UserCreateCommand(req.cpf(), req.name());
                  AccountCreateCommand accountCmd =
                      new AccountCreateCommand(
                          req.email(), AccountType.STUDENT, req.password(), userCmd);
                  Campi campusEnum = Campi.valueOf(req.campus().trim().toUpperCase(Locale.ROOT));
                  return new StudentCreateCommand(
                      accountCmd,
                      req.academicRegistration(),
                      campusEnum,
                      req.courseId(),
                      req.requiredHours(),
                      BigDecimal.ZERO,
                      req.startDate(),
                      req.dueDate());
                })
            .collect(Collectors.toList());

    List<Student> saved = writeService.saveAll(studentCommands);
    return Response.status(Response.Status.CREATED)
        .entity(ApiEnvelope.created(BulkCreateResult.sizeOnly(saved.size())))
        .build();
  }

  /**
   * Updates an existing student's details.
   *
   * @param id the UUID of the student's account to update
   * @param req the request containing updated student details
   * @return a Response with the updated student details
   */
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid StudentUpdateRequest req) {
    UserUpdateCommand userCmd = null;
    if (req.name() != null) {
      userCmd = new UserUpdateCommand(null, req.name());
    }

    AccountUpdateCommand accountCmd = null;
    if (req.email() != null || req.password() != null || userCmd != null) {
      accountCmd = new AccountUpdateCommand(req.email(), req.password(), userCmd);
    }

    Campi campusEnum = null;
    if (req.campus() != null) {
      campusEnum = Campi.valueOf(req.campus().trim().toUpperCase(Locale.ROOT));
    }

    StudentUpdateCommand studentCmd =
        new StudentUpdateCommand(
            accountCmd,
            req.academicRegistration(),
            campusEnum,
            req.courseId(),
            req.requiredHours(),
            req.completedHours(),
            req.startDate(),
            req.dueDate());

    writeService.update(id, studentCmd);

    var view = readService.getView(id);
    var body = StudentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Lists all students or searches for students based on a query.
   *
   * @param q the search query (optional)
   * @param academicRegistration the academic registration to filter by (optional, busca exata)
   * @return a Response with the list of students
   */
  @GET
  public Response listOrSearch(
      @QueryParam("q") String q, @QueryParam("academicRegistration") String academicRegistration) {

    List<StudentView> views;
    if (!StringUtils.isEmpty(academicRegistration)) {
      try {
        StudentView studentView = readService.getViewByAcademicRegistration(academicRegistration);
        views = List.of(studentView);
      } catch (ResourceNotFoundException e) {
        views = List.of();
      }
    } else if (!StringUtils.isEmpty(q)) {
      views = readService.searchByName(q);
    } else {
      views = readService.listViews();
    }
    List<StudentResponse> body =
        views.stream().map(v -> StudentPresenter.toResponse(v, locale(), i18n)).toList();
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a specific student's details by ID (Account ID).
   *
   * @param id the UUID of the student's account
   * @return a Response with the student's details
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    Objects.requireNonNull(id, "id");
    var view = readService.getView(id);
    return Response.ok(ApiEnvelope.ok(StudentPresenter.toResponse(view, locale(), i18n))).build();
  }

  /**
   * Retrieves a specific student's details by Academic Registration.
   *
   * @param academicRegistration the academic registration of the student
   * @return a Response with the student's details
   */
  @GET
  @Path("/by-registration/{academicRegistration}")
  public Response getByAcademicRegistration(
      @PathParam("academicRegistration") String academicRegistration) {
    Objects.requireNonNull(academicRegistration, "academicRegistration");
    var view = readService.getViewByAcademicRegistration(academicRegistration);
    return Response.ok(ApiEnvelope.ok(StudentPresenter.toResponse(view, locale(), i18n))).build();
  }

  /**
   * Retrieves multiple students' details by their Account IDs.
   *
   * @param req the request containing student account IDs
   * @return a Response with the list of students' details
   */
  @GET
  @Path("/bulk")
  public Response getBulk(@Valid UuidsRequest req) {
    Objects.requireNonNull(req, "ids");
    var views = readService.listViewsByIds(req.ids());
    List<StudentResponse> body =
        views.stream().map(v -> StudentPresenter.toResponse(v, locale(), i18n)).toList();
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Deletes multiple students by their Account IDs.
   *
   * @param req the request containing student account IDs to delete
   * @return a Response with the result of the deletion
   */
  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    Objects.requireNonNull(req, "req");
    Map<DeleteKeys, Long> deleted = writeService.deleteAll(req.ids());
    return Response.ok(ApiEnvelope.ok(new DeleteResult(deleted))).build();
  }
}
