package br.org.catolicasc.pug.partner.service.utils;

import br.org.catolicasc.pug.partner.domain.enums.PartnerErrorCodes;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.ws.rs.NotAuthorizedException;

public final class ExceptionHelper {

  private ExceptionHelper() {}

  public static DuplicateResourceException entityAlreadyExists() {
    return new DuplicateResourceException(PartnerErrorCodes.ENTITY_ALREADY_EXISTS);
  }

  public static BusinessRuleException entityHasProjects() {
    return new BusinessRuleException(PartnerErrorCodes.ENTITY_HAS_PROJECTS);
  }

  public static ResourceNotFoundException entityNotFound() {
    return new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND);
  }

  public static DuplicateResourceException staffAlreadyExists() {
    return new DuplicateResourceException(PartnerErrorCodes.STAFF_ALREADY_EXISTS);
  }

  public static BusinessRuleException staffAssignedToOtherEntity() {
    return new BusinessRuleException(PartnerErrorCodes.STAFF_ASSIGNED_TO_OTHER_ENTITY);
  }

  public static BusinessRuleException staffEmailAlreadyExistsInEntity() {
    return new BusinessRuleException(PartnerErrorCodes.STAFF_EMAIL_ALREADY_EXISTS_IN_ENTITY);
  }

  public static BusinessRuleException staffHasAttendances() {
    return new BusinessRuleException(PartnerErrorCodes.STAFF_HAS_ATTENDANCES);
  }

  public static BusinessRuleException staffHasProjects() {
    return new BusinessRuleException(PartnerErrorCodes.STAFF_HAS_PROJECTS);
  }

  public static ResourceNotFoundException staffNotFound() {
    return new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND);
  }

  public static NotAuthorizedException unauthorized() {
    return new NotAuthorizedException("Invalid credentials or inactive account");
  }
}
