package com.pug.academic.presenter;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.enums.Campi;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;
import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.academic.presenter.dtos.StudentCreateRequest;
import com.pug.academic.presenter.dtos.StudentResponse;
import com.pug.academic.presenter.mappers.StudentPresenter;
import com.pug.academic.service.StudentReadService;
import com.pug.academic.service.StudentService;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.identity.service.AccountService;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.dtos.BulkCreateRequest;
import com.pug.shared.presenter.dtos.BulkCreateResult;
import com.pug.shared.presenter.dtos.DeleteResult;
import com.pug.shared.presenter.dtos.UuidsRequest;
import com.pug.shared.presenter.rest.ApiEnvelope;
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
import java.util.Objects;
import java.util.UUID;

/** REST resource for managing students in the academic system. */
@ApplicationScoped
@Path("/academic/students")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentResource {

  @Inject StudentService writeService;
  @Inject StudentReadService readService;
  @Inject AccountService users;
  @Inject I18n i18n;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Creates a new student in the academic system.
   *
   * @param req the request containing student details
   * @return a Response with the created student details
   */
  @POST
  public Response create(@Valid StudentCreateRequest req) {
    Objects.requireNonNull(req, "req");
    Student created =
        writeService.save(
            new Cpf(req.cpf()),
            req.name(),
            new Email(req.email()),
            req.password(),
            new AcademicRegistration(req.academicRegistration()),
            Campi.valueOf(req.campus().trim().toUpperCase()),
            req.courseId(),
            new CounterpartHours(req.requiredHours(), java.math.BigDecimal.ZERO),
            new Period(req.startDate(), req.dueDate()));

    StudentView view = readService.getView(created.getUserId());
    StudentResponse body = StudentPresenter.toResponse(view, locale(), i18n);
    URI location = uri.getAbsolutePathBuilder().path(created.getUserId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Creates multiple students in bulk.
   *
   * @param req the bulk create request containing student details
   * @return a Response with the count of created students
   */
  @POST
  @Path("/bulk")
  public Response createBulk(@Valid BulkCreateRequest<StudentCreateRequest> req) {
    Objects.requireNonNull(req, "req");
    int createdCount = 0;
    for (StudentCreateRequest r : req.entities()) {
      writeService.save(
          new Cpf(r.cpf()),
          r.name(),
          new Email(r.email()),
          r.password(),
          new AcademicRegistration(r.academicRegistration()),
          Campi.valueOf(r.campus().trim().toUpperCase()),
          r.courseId(),
          new CounterpartHours(r.requiredHours(), java.math.BigDecimal.ZERO),
          new Period(r.startDate(), r.dueDate()));
      createdCount++;
    }
    return Response.status(Response.Status.CREATED)
        .entity(ApiEnvelope.created(BulkCreateResult.sizeOnly(createdCount)))
        .build();
  }

  /**
   * Updates an existing student's details.
   *
   * @param id the UUID of the student to update
   * @param req the request containing updated student details
   * @return a Response with the updated student details
   */
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid StudentCreateRequest req) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(req, "req");

    writeService.update(
        id,
        Campi.valueOf(req.campus().trim().toUpperCase()),
        req.courseId(),
        new Period(req.startDate(), req.dueDate()));

    var view = readService.getView(id);
    var body = StudentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Lists all students or searches for students based on a query.
   *
   * @param q the search query (optional)
   * @return a Response with the list of students
   */
  @GET
  public Response listOrSearch(@QueryParam("q") String q) {
    List<StudentView> views;
    if (q == null || q.isBlank()) {
      views = readService.listViews();
    } else {
      var ids =
          users.search(q).stream()
              .filter(u -> u.getAccountType() == AccountType.STUDENT)
              .map(u -> u.getId())
              .toList();
      views = readService.listViewsByIds(ids);
    }
    List<StudentResponse> body =
        views.stream().map(v -> StudentPresenter.toResponse(v, locale(), i18n)).toList();
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a specific student's details by ID.
   *
   * @param id the UUID of the student
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
   * Retrieves multiple students' details by their IDs.
   *
   * @param req the request containing student IDs
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
   * Deletes multiple students by their IDs.
   *
   * @param req the request containing student IDs to delete
   * @return a Response with the result of the deletion
   */
  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    Objects.requireNonNull(req, "req");
    Map<String, Long> deleted = writeService.deleteAll(req.ids());
    return Response.ok(ApiEnvelope.ok(new DeleteResult(deleted))).build();
  }

  /**
   * Determines the locale from the HTTP headers.
   *
   * @return the determined Locale
   */
  private Locale locale() {
    var langs = headers.getAcceptableLanguages();
    return (langs == null || langs.isEmpty()) ? Locale.getDefault() : langs.getFirst();
  }
}
