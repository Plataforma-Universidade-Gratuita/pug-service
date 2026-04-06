package com.pug.partner.presenter.mappers;

import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.partner.presenter.dtos.EntityCreateRequest;
import com.pug.partner.presenter.dtos.EntityResponse;
import com.pug.partner.presenter.dtos.EntityUpdateRequest;
import com.pug.partner.service.dtos.EntityCreateCommand;
import com.pug.partner.service.dtos.EntityUpdateCommand;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import com.pug.shared.presenter.mappers.SharedDataPresenter;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal partner entity projections to external
 * API responses and requests to commands.
 *
 * <p>This presenter acts as a translation layer, converting incoming REST payloads into application
 * commands, and raw CQRS query views ({@link EntityView}) into client-ready representations ({@link
 * EntityResponse}).
 */
public final class EntityPresenter {

  /** Private constructor to prevent instantiation of utility class. */
  private EntityPresenter() {}

  /**
   * Maps an incoming REST creation request into an application layer creation command.
   *
   * @param req the validated {@link EntityCreateRequest} payload
   * @return the corresponding {@link EntityCreateCommand}, or {@code null} if input is null
   */
  public static EntityCreateCommand toCommand(EntityCreateRequest req) {
    if (req == null) {
      return null;
    }
    return new EntityCreateCommand(req.cnpjString(), req.name(), req.cityId(), req.address());
  }

  /**
   * Maps an incoming REST update request into an application layer update command.
   *
   * @param req the validated {@link EntityUpdateRequest} payload
   * @return the corresponding {@link EntityUpdateCommand}, or {@code null} if input is null
   */
  public static EntityUpdateCommand toCommand(EntityUpdateRequest req) {
    if (req == null) {
      return null;
    }
    return new EntityUpdateCommand(req.name(), req.cityId(), req.address());
  }

  /**
   * Computes the formatted string representation of a Brazilian CNPJ.
   *
   * <p>Transforms a raw 14-digit numeric string (e.g., "12345678000199") into the standard
   * punctuated format (e.g., "12.345.678/0001-99"). If the input is null or not exactly 14
   * characters, the raw value is returned safely to prevent exceptions.
   *
   * @param value the raw numeric CNPJ string
   * @return the punctuated CNPJ string, or the raw input if formatting is not possible
   */
  private static String toFormattedString(String value) {
    if (value == null || value.length() != 14) {
      return value;
    }
    return value.substring(0, 2)
        + "."
        + value.substring(2, 5)
        + "."
        + value.substring(5, 8)
        + "/"
        + value.substring(8, 12)
        + "-"
        + value.substring(12, 14);
  }

  /**
   * Projects a read-only {@link EntityView} into a client-facing {@link EntityResponse}.
   *
   * <p>This mapping resolves localized formatting for audit timestamps and corporate identification
   * numbers, while exposing only the {@code cityId} so that additional geographic details can be
   * resolved on demand by the client.
   *
   * @param v the internal read-model projection of the partner entity
   * @param locale the locale extracted from the client's request headers
   * @return a fully populated {@link EntityResponse} ready for JSON serialization, or {@code null}
   *     if the input view is null
   */
  public static EntityResponse toResponse(EntityView v, Locale locale) {
    if (v == null) {
      return null;
    }

    String formattedCnpj = toFormattedString(v.cnpj());
    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(v.createdAt(), v.updatedAt(), locale);

    return new EntityResponse(
        v.id(), v.cnpj(), formattedCnpj, v.name(), v.address(), v.cityId(), auditInfo);
  }
}
