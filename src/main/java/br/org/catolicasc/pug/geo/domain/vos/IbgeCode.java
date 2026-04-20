package br.org.catolicasc.pug.geo.domain.vos;

import br.org.catolicasc.pug.geo.domain.enums.GeoFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Value Object (VO) representing a Brazilian city's IBGE (Brazilian Institute of
 * Geography and Statistics) code.
 *
 * <p>Extends {@link DomainError} to encapsulate and accumulate domain validation rules specific to
 * IBGE codes without throwing immediate exceptions.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class IbgeCode extends DomainError {

  /** The raw 7-digit string representing the IBGE code. */
  String code;

  /**
   * Constructs an {@code IbgeCode} instance.
   *
   * @param code the raw IBGE code string
   */
  @Builder(toBuilder = true)
  private IbgeCode(String code) {
    this.code = code;
  }

  /**
   * Factory method to create a new {@code IbgeCode} instance.
   *
   * <p>The instance is created and immediately self-validated. Any validation failures are
   * accumulated internally and can be retrieved via {@link #getFieldErrors()}.
   *
   * @param code the raw IBGE code string
   * @return a self-validated {@link IbgeCode} instance
   */
  public static IbgeCode factory(String code) {
    IbgeCode vo = IbgeCode.builder().code(code).build();
    vo.collectValidationProblems();
    return vo;
  }

  /**
   * Evaluates internal constraints and accumulates validation problems.
   *
   * <p>Business rules applied:
   *
   * <ul>
   *   <li>Must not be null or empty (appends {@link GeoFieldErrorCodes#INVALID_IBGE_CODE_BLANK})
   *   <li>Must be exactly 7 characters long and contain only numeric digits (appends {@link
   *       GeoFieldErrorCodes#INVALID_IBGE_CODE_FORMAT})
   * </ul>
   */
  private void collectValidationProblems() {
    if (StringUtils.isEmpty(code)) {
      addFieldError(GeoFieldErrorCodes.INVALID_IBGE_CODE_BLANK);
      return;
    }
    if (code.length() != 7 || !code.chars().allMatch(Character::isDigit)) {
      addFieldError(GeoFieldErrorCodes.INVALID_IBGE_CODE_FORMAT);
    }
  }
}
