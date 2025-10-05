package com.pug.partner.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.geo.domain.City;
import com.pug.partner.domain.PartnerEntity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PartnerEntityValidationTest {

  static Validator validator;
  static Locale original;

  private static final String VALID_CNPJ = "11222333000181";

  private static City someCity() {
    return City.builder().id(null).name("Florianópolis2").ibgeCode("4205401").build();
  }

  @BeforeAll
  static void boot() {
    original = Locale.getDefault();
    Locale.setDefault(Locale.ENGLISH);
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @AfterAll
  static void tearDown() {
    Locale.setDefault(original);
  }

  @Test
  void validEntityPasses() {
    var e = PartnerEntity.builder().cnpj(VALID_CNPJ).name("Org A").city(someCity()).build();
    var v = validator.validate(e);
    assertTrue(v.isEmpty());
  }

  @Test
  void cnpj_acceptsMasked_beforePersisting() {
    var e =
        PartnerEntity.builder().cnpj("11.222.333/0001-81").name("Org A").city(someCity()).build();
    var v = validator.validate(e);
    assertTrue(v.isEmpty());
  }

  @Test
  void cnpjCannotBeBlankOrNull() {
    var blank = PartnerEntity.builder().cnpj("  ").name("Org").city(someCity()).build();
    var vBlank = validator.validate(blank);
    var templatesBlank =
        vBlank.stream()
            .filter(cv -> cv.getPropertyPath().toString().equals("cnpj"))
            .map(ConstraintViolation::getMessageTemplate)
            .toList();
    assertTrue(templatesBlank.contains("{jakarta.validation.constraints.NotBlank.message}"));
    assertTrue(templatesBlank.contains("{org.hibernate.validator.constraints.br.CNPJ.message}"));

    var nul = PartnerEntity.builder().cnpj(null).name("Org").city(someCity()).build();
    var vNull = validator.validate(nul);
    var cvNull = one(vNull, "cnpj");
    assertEquals("{jakarta.validation.constraints.NotBlank.message}", cvNull.getMessageTemplate());
  }

  @Test
  void cnpjWithLettersFailsCnpjConstraint() {
    var e = PartnerEntity.builder().cnpj("11222333000A81").name("Org").city(someCity()).build();
    var v = validator.validate(e);
    var cv = one(v, "cnpj");
    assertEquals("{org.hibernate.validator.constraints.br.CNPJ.message}", cv.getMessageTemplate());
  }

  @Test
  void nameCannotBeBlankOrNull() {
    var blank = PartnerEntity.builder().cnpj(VALID_CNPJ).name(" ").city(someCity()).build();
    var vBlank = validator.validate(blank);
    var cvBlank = one(vBlank, "name");
    assertEquals("{jakarta.validation.constraints.NotBlank.message}", cvBlank.getMessageTemplate());

    var nul = PartnerEntity.builder().cnpj(VALID_CNPJ).name(null).city(someCity()).build();
    var vNull = validator.validate(nul);
    var cvNull = one(vNull, "name");
    assertEquals("{jakarta.validation.constraints.NotBlank.message}", cvNull.getMessageTemplate());
  }

  @Test
  void nameMax150Chars() {
    var e = PartnerEntity.builder().cnpj(VALID_CNPJ).name("x".repeat(151)).city(someCity()).build();
    var v = validator.validate(e);
    var cv = one(v, "name");
    assertEquals("{jakarta.validation.constraints.Size.message}", cv.getMessageTemplate());
  }

  @Test
  void cityCannotBeNull() {
    var e = PartnerEntity.builder().cnpj(VALID_CNPJ).name("Org").city(null).build();
    var v = validator.validate(e);
    var cv = one(v, "city");
    assertEquals("{jakarta.validation.constraints.NotNull.message}", cv.getMessageTemplate());
  }

  @Test
  void addressMax254() {
    var e =
        PartnerEntity.builder()
            .cnpj(VALID_CNPJ)
            .name("Org")
            .city(someCity())
            .address("y".repeat(255))
            .build();
    var v = validator.validate(e);
    var cv = one(v, "address");
    assertEquals("{jakarta.validation.constraints.Size.message}", cv.getMessageTemplate());
  }

  private static ConstraintViolation<PartnerEntity> one(
      Set<ConstraintViolation<PartnerEntity>> v, String prop) {
    List<ConstraintViolation<PartnerEntity>> list =
        v.stream().filter(cv -> cv.getPropertyPath().toString().equals(prop)).toList();
    assertEquals(1, list.size());
    return list.getFirst();
  }
}
