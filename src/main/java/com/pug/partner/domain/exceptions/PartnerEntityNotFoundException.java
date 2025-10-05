package com.pug.partner.domain.exceptions;

import static com.pug.shared.errors.ErrorCodes.ENTITY_NOT_FOUND;

import com.pug.shared.errors.DomainException;
import java.util.UUID;

public final class PartnerEntityNotFoundException extends DomainException {
  public PartnerEntityNotFoundException(UUID id) {
    super(ENTITY_NOT_FOUND, id);
  }

  public PartnerEntityNotFoundException(String cnpj) {
    super(ENTITY_NOT_FOUND, cnpj);
  }
}
