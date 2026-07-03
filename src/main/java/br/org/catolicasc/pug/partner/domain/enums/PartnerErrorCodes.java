/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.partner.domain.enums;

import br.org.catolicasc.pug.shared.domain.enums.GenericCodes;
import lombok.Getter;

/**
 * Error codes emitted by partner-domain and partner-service workflows.
 *
 * <p>Each enum constant resolves to a message-bundle key consumed by exception mappers and
 * localized API error payloads.
 */
@Getter
public enum PartnerErrorCodes implements GenericCodes {
  ENTITY_ALREADY_EXISTS("error.domain.partner.entity.exists"),
  ENTITY_HAS_PROJECTS("error.domain.partner.entity.has.projects"),
  ENTITY_NOT_FOUND("error.domain.partner.entity.not.found"),
  STAFF_ALREADY_EXISTS("error.domain.partner.staff.exists"),
  STAFF_ASSIGNED_TO_OTHER_ENTITY("error.domain.partner.staff.assigned.to.other.entity"),
  STAFF_EMAIL_ALREADY_EXISTS_IN_ENTITY("error.domain.partner.staff.email.exists.in.entity"),
  STAFF_HAS_ATTENDANCES("error.domain.partner.staff.has.attendances"),
  STAFF_HAS_PROJECTS("error.domain.partner.staff.has.projects"),
  STAFF_NOT_FOUND("error.domain.partner.staff.not.found");

  private final String bundleKey;

  PartnerErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
