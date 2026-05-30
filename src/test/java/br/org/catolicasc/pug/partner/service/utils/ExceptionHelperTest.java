package br.org.catolicasc.pug.partner.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.partner.domain.enums.PartnerErrorCodes;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.ws.rs.NotAuthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Partner ExceptionHelper Coverage")
class ExceptionHelperTest {

  @Test
  @DisplayName("Should create entity duplication exception")
  void entityAlreadyExists() {
    DuplicateResourceException exception = ExceptionHelper.entityAlreadyExists();

    assertThat(exception.getCode()).isEqualTo(PartnerErrorCodes.ENTITY_ALREADY_EXISTS);
  }

  @Test
  @DisplayName("Should create entity has projects exception")
  void entityHasProjects() {
    BusinessRuleException exception = ExceptionHelper.entityHasProjects();

    assertThat(exception.getCode()).isEqualTo(PartnerErrorCodes.ENTITY_HAS_PROJECTS);
  }

  @Test
  @DisplayName("Should create entity not found exception")
  void entityNotFound() {
    ResourceNotFoundException exception = ExceptionHelper.entityNotFound();

    assertThat(exception.getCode()).isEqualTo(PartnerErrorCodes.ENTITY_NOT_FOUND);
  }

  @Test
  @DisplayName("Should create staff duplication exception")
  void staffAlreadyExists() {
    DuplicateResourceException exception = ExceptionHelper.staffAlreadyExists();

    assertThat(exception.getCode()).isEqualTo(PartnerErrorCodes.STAFF_ALREADY_EXISTS);
  }

  @Test
  @DisplayName("Should create staff assigned to other entity exception")
  void staffAssignedToOtherEntity() {
    BusinessRuleException exception = ExceptionHelper.staffAssignedToOtherEntity();

    assertThat(exception.getCode()).isEqualTo(PartnerErrorCodes.STAFF_ASSIGNED_TO_OTHER_ENTITY);
  }

  @Test
  @DisplayName("Should create staff email already exists in entity exception")
  void staffEmailAlreadyExistsInEntity() {
    BusinessRuleException exception = ExceptionHelper.staffEmailAlreadyExistsInEntity();

    assertThat(exception.getCode())
        .isEqualTo(PartnerErrorCodes.STAFF_EMAIL_ALREADY_EXISTS_IN_ENTITY);
  }

  @Test
  @DisplayName("Should create staff has attendances exception")
  void staffHasAttendances() {
    BusinessRuleException exception = ExceptionHelper.staffHasAttendances();

    assertThat(exception.getCode()).isEqualTo(PartnerErrorCodes.STAFF_HAS_ATTENDANCES);
  }

  @Test
  @DisplayName("Should create staff has projects exception")
  void staffHasProjects() {
    BusinessRuleException exception = ExceptionHelper.staffHasProjects();

    assertThat(exception.getCode()).isEqualTo(PartnerErrorCodes.STAFF_HAS_PROJECTS);
  }

  @Test
  @DisplayName("Should create staff not found exception")
  void staffNotFound() {
    ResourceNotFoundException exception = ExceptionHelper.staffNotFound();

    assertThat(exception.getCode()).isEqualTo(PartnerErrorCodes.STAFF_NOT_FOUND);
  }

  @Test
  @DisplayName("Should create unauthorized exception")
  void unauthorized() {
    NotAuthorizedException exception = ExceptionHelper.unauthorized();

    assertThat(exception.getResponse().getStatus()).isEqualTo(401);
  }
}
