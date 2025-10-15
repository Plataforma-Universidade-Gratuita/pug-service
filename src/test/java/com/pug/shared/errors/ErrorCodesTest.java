package com.pug.shared.errors;

import static com.pug.academic.domain.AcademicErrorCodes.ACADEMIC_COURSE_NAME_REQUIRED;
import static com.pug.academic.domain.AcademicErrorCodes.ACADEMIC_COURSE_NAME_TOO_LONG;
import static com.pug.academic.domain.AcademicErrorCodes.ACADEMIC_COURSE_SCHOOL_REQUIRED;
import static com.pug.academic.domain.AcademicErrorCodes.ACADEMIC_SCHOOL_NAME_REQUIRED;
import static com.pug.academic.domain.AcademicErrorCodes.ACADEMIC_SCHOOL_NAME_TOO_LONG;
import static com.pug.geo.domain.GeoErrorCodes.GEO_IBGE_INVALID;
import static com.pug.geo.domain.GeoErrorCodes.GEO_NAME_REQUIRED;
import static com.pug.geo.domain.GeoErrorCodes.GEO_NAME_TOO_LONG;
import static com.pug.identity.domain.IdentityErrorCodes.IDENTITY_CPF_ALREADY_IN_USE;
import static com.pug.identity.domain.IdentityErrorCodes.IDENTITY_CPF_INVALID;
import static com.pug.identity.domain.IdentityErrorCodes.IDENTITY_CPF_REQUIRED;
import static com.pug.identity.domain.IdentityErrorCodes.IDENTITY_NAME_REQUIRED;
import static com.pug.identity.domain.IdentityErrorCodes.IDENTITY_NAME_TOO_LONG;
import static com.pug.identity.domain.IdentityErrorCodes.IDENTITY_NOT_FOUND;
import static com.pug.partner.domain.PartnerErrorCodes.PARTNER_ADDRESS_TOO_LONG;
import static com.pug.partner.domain.PartnerErrorCodes.PARTNER_CITY_REQUIRED;
import static com.pug.partner.domain.PartnerErrorCodes.PARTNER_CNPJ_ALREADY_EXISTS;
import static com.pug.partner.domain.PartnerErrorCodes.PARTNER_CNPJ_INVALID;
import static com.pug.partner.domain.PartnerErrorCodes.PARTNER_CNPJ_LENGTH;
import static com.pug.partner.domain.PartnerErrorCodes.PARTNER_CNPJ_REQUIRED;
import static com.pug.partner.domain.PartnerErrorCodes.PARTNER_NAME_REQUIRED;
import static com.pug.partner.domain.PartnerErrorCodes.PARTNER_NAME_TOO_LONG;
import static com.pug.partner.domain.PartnerErrorCodes.PARTNER_NOT_FOUND;
import static com.pug.partner.domain.PartnerErrorCodes.STAFF_EMAIL_ALREADY_EXISTS;
import static com.pug.partner.domain.PartnerErrorCodes.STAFF_EMAIL_INVALID;
import static com.pug.partner.domain.PartnerErrorCodes.STAFF_EMAIL_REQUIRED;
import static com.pug.partner.domain.PartnerErrorCodes.STAFF_EMAIL_TOO_LONG;
import static com.pug.partner.domain.PartnerErrorCodes.STAFF_ENTITY_REQUIRED;
import static com.pug.partner.domain.PartnerErrorCodes.STAFF_NOT_FOUND;
import static com.pug.partner.domain.PartnerErrorCodes.STAFF_USER_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ErrorCodesTest {

  @Test
  void bundleKeyCoversAllKnownCodes() {
    var cases =
        Map.ofEntries(
            Map.entry(ErrorCodes.VALIDATION_ERROR, "error.validation"),
            Map.entry(ErrorCodes.INTERNAL_ERROR, "error.internal"),
            // geo
            Map.entry(GEO_NAME_REQUIRED, "error.geo.city.name.required"),
            Map.entry(GEO_NAME_TOO_LONG, "error.geo.city.name.toolong"),
            Map.entry(GEO_IBGE_INVALID, "error.geo.city.ibge.invalid"),
            // identity
            Map.entry(IDENTITY_CPF_INVALID, "error.identity.user.cpf.invalid"),
            Map.entry(IDENTITY_CPF_REQUIRED, "error.identity.user.cpf.required"),
            Map.entry(IDENTITY_NAME_REQUIRED, "error.identity.user.name.required"),
            Map.entry(IDENTITY_NAME_TOO_LONG, "error.identity.user.name.toolong"),
            Map.entry(IDENTITY_NOT_FOUND, "error.identity.user.notfound"),
            Map.entry(IDENTITY_CPF_ALREADY_IN_USE, "error.identity.user.cpf.alreadyinuse"),
            // partner
            Map.entry(PARTNER_ADDRESS_TOO_LONG, "error.partner.entity.address.toolong"),
            Map.entry(PARTNER_CNPJ_REQUIRED, "error.partner.entity.cnpj.required"),
            Map.entry(PARTNER_CNPJ_INVALID, "error.partner.entity.cnpj.invalid"),
            Map.entry(PARTNER_CNPJ_LENGTH, "error.partner.entity.cnpj.length"),
            Map.entry(PARTNER_NAME_REQUIRED, "error.partner.entity.name.required"),
            Map.entry(PARTNER_NAME_TOO_LONG, "error.partner.entity.name.toolong"),
            Map.entry(PARTNER_CITY_REQUIRED, "error.partner.entity.city.required"),
            Map.entry(PARTNER_NOT_FOUND, "error.partner.entity.notfound"),
            Map.entry(PARTNER_CNPJ_ALREADY_EXISTS, "error.partner.entity.cnpj.alreadyinuse"),
            Map.entry(STAFF_USER_REQUIRED, "error.partner.staff.user.required"),
            Map.entry(STAFF_ENTITY_REQUIRED, "error.partner.staff.entity.required"),
            Map.entry(STAFF_EMAIL_REQUIRED, "error.partner.staff.email.required"),
            Map.entry(STAFF_EMAIL_TOO_LONG, "error.partner.staff.email.toolong"),
            Map.entry(STAFF_NOT_FOUND, "error.partner.staff.notfound"),
            Map.entry(STAFF_EMAIL_ALREADY_EXISTS, "error.partner.staff.email.alreadyinuse"),
            Map.entry(STAFF_EMAIL_INVALID, "error.partner.staff.email.invalid"),
            // academic
            Map.entry(ACADEMIC_SCHOOL_NAME_REQUIRED, "error.academic.school.name.required"),
            Map.entry(ACADEMIC_SCHOOL_NAME_TOO_LONG, "error.academic.school.name.toolong"),
            Map.entry(ACADEMIC_COURSE_NAME_REQUIRED, "error.academic.course.name.required"),
            Map.entry(ACADEMIC_COURSE_NAME_TOO_LONG, "error.academic.course.name.toolong"),
            Map.entry(ACADEMIC_COURSE_SCHOOL_REQUIRED, "error.academic.course.school.required"));

    cases.forEach((code, expected) -> assertEquals(expected, ErrorCodes.bundleKey(code), code));
  }

  @Test
  void unknownCodeFallsBackToItself() {
    assertEquals("SOMETHING_NEW", ErrorCodes.bundleKey("SOMETHING_NEW"));
  }
}
