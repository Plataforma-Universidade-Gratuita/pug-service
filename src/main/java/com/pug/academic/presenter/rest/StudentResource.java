// src/main/java/com/pug/academic/presenter/rest/StudentResource.java
package com.pug.academic.presenter.rest;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.domain.enums.Campi;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;
import com.pug.academic.presenter.dtos.StudentCreateRequest;
import com.pug.academic.presenter.dtos.StudentView;
import com.pug.academic.service.StudentReadService;
import com.pug.academic.service.StudentService;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.identity.service.UserService;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.presenter.dtos.BulkCreateResult;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.validation.UuidV7;
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
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
@Path("/students")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentResource {

    @Inject
    StudentReadService read;
    @Inject
    StudentService service;
    @Inject
    UserService users;
    @Context
    HttpHeaders headers;

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") @UuidV7 UUID id) {
        StudentView v = read.getView(id);
        if (v == null) throw new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND);
        return Response.ok(ApiEnvelope.ok(v)).build();
    }

    @GET
    public Response list(@QueryParam("courseId") UUID courseId) {
        List<StudentView> list = (courseId == null) ? read.listViews() : read.listViewsByCourseId(courseId);
        return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(list))).build();
    }

    @GET
    @Path("by-cpf/{cpf}")
    public Response getByCpf(@PathParam("cpf") String cpfRaw) {
        String cpf = new Cpf(cpfRaw).toString();
        var results =
                users.listByCpf(cpf).stream()
                        .filter(u -> u.getAccountType() == AccountType.STUDENT)
                        .map(u -> read.getView(u.getId()))
                        .filter(Objects::nonNull)
                        .toList();
        if (results.isEmpty()) throw new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND);
        return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(results))).build();
    }

    @GET
    @Path("by-email")
    public Response getByEmail(@QueryParam("email") String email) {
        if (email == null || email.isBlank()) return list(null);
        var u = users.getByEmail(email);
        if (u.getAccountType() != AccountType.STUDENT) {
            throw new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND);
        }
        var v = read.getView(u.getId());
        if (v == null) throw new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND);
        return Response.ok(ApiEnvelope.ok(v)).build();
    }

    @GET
    @Path("by-name")
    public Response listByName(@QueryParam("q") String query) {
        if (query == null || query.isBlank()) return list(null);
        var results =
                users.search(query).stream()
                        .filter(u -> u.getAccountType() == AccountType.STUDENT)
                        .map(u -> read.getView(u.getId()))
                        .filter(Objects::nonNull)
                        .toList();
        return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(results))).build();
    }

    @POST
    public Response create(@Valid StudentCreateRequest req, @Context UriInfo uri) {
        var s =
                service.save(
                        new Cpf(req.cpf()),
                        req.name(),
                        new Email(req.email()),
                        req.password(),
                        new AcademicRegistration(req.academicRegistration()),
                        Campi.valueOf(req.campus().trim().toUpperCase()),
                        req.courseId(),
                        new CounterpartHours(req.requiredHours(), java.math.BigDecimal.ZERO),
                        new Period(req.startDate(), req.dueDate()));
        StudentView v = read.getView(s.getUserId());
        if (v == null) throw new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND);
        URI location = uri.getAbsolutePathBuilder().path(s.getUserId().toString()).build();
        return Response.created(location).entity(ApiEnvelope.created(v)).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") @UuidV7 UUID id) {
        service.revoke(id);
        return Response.noContent().build();
    }
}
