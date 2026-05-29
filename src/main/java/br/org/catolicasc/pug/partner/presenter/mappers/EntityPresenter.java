package br.org.catolicasc.pug.partner.presenter.mappers;

import br.org.catolicasc.pug.partner.infra.read.dtos.EntityComplexSearchView;
import br.org.catolicasc.pug.partner.infra.read.dtos.EntityView;
import br.org.catolicasc.pug.partner.presenter.dtos.EntityComplexSearchResponse;
import br.org.catolicasc.pug.partner.presenter.dtos.EntityCreateRequest;
import br.org.catolicasc.pug.partner.presenter.dtos.EntityResponse;
import br.org.catolicasc.pug.partner.presenter.dtos.EntitySimpleComplexSearchResponse;
import br.org.catolicasc.pug.partner.presenter.dtos.EntityUpdateRequest;
import br.org.catolicasc.pug.partner.service.dtos.EntityCreateCommand;
import br.org.catolicasc.pug.partner.service.dtos.EntityUpdateCommand;
import br.org.catolicasc.pug.geo.presenter.dtos.CityResponse;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import br.org.catolicasc.pug.shared.presenter.mappers.SharedDataPresenter;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal partner entity projections to external
 * API responses and requests to commands.
 */
public final class EntityPresenter {

  private EntityPresenter() {}

  public static EntityCreateCommand toCommand(EntityCreateRequest req) {
    if (req == null) {
      return null;
    }
    return new EntityCreateCommand(req.cnpjString(), req.name(), req.cityId(), req.address());
  }

  public static EntityUpdateCommand toCommand(EntityUpdateRequest req) {
    if (req == null) {
      return null;
    }
    return new EntityUpdateCommand(req.name(), req.cityId(), req.address());
  }

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
   * Projects a partner-entity complex-search projection into a client-facing response item.
   *
   * @param view the read-side projection produced by the infrastructure query
   * @param locale the locale used to format audit timestamps
   * @return the mapped API response item, or {@code null} when the input view is {@code null}
   */
  public static EntityComplexSearchResponse toComplexSearchResponse(
      EntityComplexSearchView view, Locale locale) {
    if (view == null) {
      return null;
    }

    return new EntityComplexSearchResponse(
        view.id(),
        view.cnpj(),
        toFormattedString(view.cnpj()),
        view.name(),
        view.address(),
        new CityResponse(view.cityId(), view.cityName(), view.cityIbgeCode()),
        SharedDataPresenter.createAuditInfoResponse(view.createdAt(), view.updatedAt(), locale));
  }

  /**
   * Projects a partner-entity search projection into a minimal response item containing only the
   * fields required by lightweight consumers such as the staff complex-search contract.
   *
   * @param view the read-side projection produced by the infrastructure query
   * @return the mapped lightweight response item, or {@code null} when the input view is {@code
   *     null}
   */
  public static EntitySimpleComplexSearchResponse toSimpleComplexSearchResponse(
      EntityComplexSearchView view) {
    if (view == null) {
      return null;
    }
    return new EntitySimpleComplexSearchResponse(view.id(), view.name());
  }

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
