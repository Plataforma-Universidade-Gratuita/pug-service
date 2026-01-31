package com.pug.geo.domain.vos;

import com.pug.geo.domain.enums.GeoErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** Value object representing a city's IBGE code. Converted to class to extend DomainError. */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class IbgeCode extends DomainError {

  String code;

  @Builder(toBuilder = true)
  private IbgeCode(String code) {
    this.code = code;
  }

  /**
   * Factory method to create a new IbgeCode. It does not throw exceptions immediately but collects
   * them in the problems list.
   *
   * @param code The IBGE code string
   * @return The IbgeCode instance (which may contain errors)
   */
  public static IbgeCode factory(String code) {
    IbgeCode vo = IbgeCode.builder().code(code).build();
    vo.validate();
    return vo;
  }

  /** Validates the IBGE code format and populates the problems list if invalid. */
  private void validate() {
    if (StringUtils.isEmpty(code)) {
      getProblems().add(new AppValidationException.Problem(GeoErrorCodes.INVALID_IBGE_CODE_BLANK));
    } else {
      if (code.length() != 7 || !code.chars().allMatch(Character::isDigit)) {
        getProblems()
            .add(new AppValidationException.Problem(GeoErrorCodes.INVALID_IBGE_CODE_FORMAT));
      }
    }
  }
}
