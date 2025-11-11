package com.pug.identity.presenter;

import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.presenter.dtos.UserResponse;
import com.pug.identity.presenter.mappers.UserPresenter;
import com.pug.identity.service.UserReadService;
import com.pug.shared.presenter.dtos.BulkCreateResult;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.utils.PresenterUtils;
import com.pug.shared.utils.StringUtils;
import com.pug.shared.validation.UuidV7;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/identity/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserReadOnlyResource {

    @Inject
    UserReadService readService;

    @Context
    HttpHeaders headers;

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the UUID of the user
     * @return the response containing the user data
     */
    @GET
    @Path("{id}")
    public Response get(@PathParam("id") @UuidV7 UUID id) {
        var locale = PresenterUtils.pickLocale(headers.getAcceptableLanguages());
        UserResponse body = UserPresenter.toResponse(readService.getView(id), locale);
        return Response.ok(ApiEnvelope.ok(body)).build();
    }

    /**
     * Lists all users.
     *
     * @return the response containing the list of users
     */
    @GET
    public Response list() {
        var locale = PresenterUtils.pickLocale(headers.getAcceptableLanguages());
        List<UserResponse> list =
                readService.listViews().stream().map(v -> UserPresenter.toResponse(v, locale)).toList();
        return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(list))).build();
    }

    /**
     * Retrieves users by their CPF.
     *
     * @param cpfRaw the CPF of the users
     * @return the response containing the list of users with the specified CPF
     */
    @GET
    @Path("by-cpf/{cpf}")
    public Response getByCpf(@PathParam("cpf") @NotNull String cpfRaw) {
        var locale = PresenterUtils.pickLocale(headers.getAcceptableLanguages());
        String cpf = new Cpf(cpfRaw).toString();
        UserResponse body =
                UserPresenter.toResponse(
                        readService.getViewByCpf(cpf), locale);
        return Response.ok(ApiEnvelope.ok(body)).build();
    }

    /**
     * Lists users by their name matching the query.
     *
     * @param query the name query to search for
     * @return the response containing the list of users matching the name query
     */
    @GET
    @Path("by-name")
    public Response listByName(@QueryParam("q") String query) {
        var body = new ArrayList<UserResponse>();
        if (StringUtils.isEmpty(query)) {
            return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(body))).build();
        }
        var locale = PresenterUtils.pickLocale(headers.getAcceptableLanguages());
        List<UserResponse> list =
                readService.search(query).stream()
                        .map(v -> UserPresenter.toResponse(v, locale))
                        .toList();
        return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(list))).build();
    }
}
