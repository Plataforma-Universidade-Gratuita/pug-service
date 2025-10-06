package com.pug.partner.domain.staff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.geo.domain.City;
import com.pug.identity.domain.Role;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.UserRole;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.Staff;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class StaffValidationTest {

  static Validator validator;
  static Locale original;

  @BeforeAll
  static void boot() {
    original = Locale.getDefault();
    Locale.setDefault(Locale.ENGLISH);
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @AfterAll
  static void down() {
    Locale.setDefault(original);
  }

  private static Staff validStaff() {
    var city =
        City.builder().id(java.util.UUID.randomUUID()).name("City").ibgeCode("4200000").build();
    var ent =
        PartnerEntity.builder()
            .id(java.util.UUID.randomUUID())
            .cnpj("11222333000181")
            .name("Org")
            .city(city)
            .build();
    var user =
        User.builder().id(java.util.UUID.randomUUID()).cpf("93541134780").name("Ada").build();
    var role =
        Role.builder()
            .id(java.util.UUID.randomUUID())
            .user(user)
            .email("a@b.org")
            .role(UserRole.ADMIN)
            .build();
    return Staff.builder().entity(ent).userRole(role).build();
  }

  @Test
  void validStaffPasses() {
    var s = validStaff();
    var v = validator.validate(s);
    assertTrue(v.isEmpty());
  }

  @Test
  void userRoleCannotBeNull() {
    var s = validStaff();
    s.setUserRole(null);
    var v = validator.validate(s);
    var cv = one(v, "userRole");
    assertEquals("{jakarta.validation.constraints.NotNull.message}", cv.getMessageTemplate());
  }

  @Test
  void entityCannotBeNull() {
    var s = validStaff();
    s.setEntity(null);
    var v = validator.validate(s);
    var cv = one(v, "entity");
    assertEquals("{jakarta.validation.constraints.NotNull.message}", cv.getMessageTemplate());
  }

  private static ConstraintViolation<Staff> one(Set<ConstraintViolation<Staff>> v, String prop) {
    List<ConstraintViolation<Staff>> list =
        v.stream().filter(cv -> cv.getPropertyPath().toString().equals(prop)).toList();
    assertEquals(1, list.size());
    return list.getFirst();
  }
}
