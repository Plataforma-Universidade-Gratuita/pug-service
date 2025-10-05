package com.pug.identity.domain.RoleTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.identity.domain.Role;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.UserRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RoleValidationTest {
  static Validator validator;
  static Locale original;

  private static final String VALID_CPF = "93541134780";

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

  private static User stubUser() {
    return User.builder().id(UUID.randomUUID()).cpf(VALID_CPF).name("Ada Lovelace").build();
  }

  @Test
  void validAssignmentPassesAndDefaultsActiveTrue() {
    var a = Role.builder().user(stubUser()).role(UserRole.ADMIN).email("admin@example.org").build();
    var v = validator.validate(a);
    assertTrue(v.isEmpty());
    assertTrue(a.isActive());
  }

  @Test
  void userCannotBeNull() {
    var a = Role.builder().user(null).role(UserRole.ADMIN).email("a@ex.org").build();
    var v = validator.validate(a);
    var cv = one(v, "user");
    assertEquals("{jakarta.validation.constraints.NotNull.message}", cv.getMessageTemplate());
  }

  @Test
  void roleCannotBeNull() {
    var a = Role.builder().user(stubUser()).role(null).email("a@ex.org").build();
    var v = validator.validate(a);
    var cv = one(v, "role");
    assertEquals("{jakarta.validation.constraints.NotNull.message}", cv.getMessageTemplate());
  }

  @Test
  void emailCannotBeBlank() {
    var a = Role.builder().user(stubUser()).role(UserRole.ADMIN).email(" ").build();
    var v = validator.validate(a);

    var templates =
        v.stream()
            .filter(cv -> cv.getPropertyPath().toString().equals("email"))
            .map(ConstraintViolation::getMessageTemplate)
            .toList();

    assertTrue(templates.contains("{jakarta.validation.constraints.NotBlank.message}"));
    assertTrue(templates.contains("{jakarta.validation.constraints.Email.message}"));
  }

  @Test
  void emailMustBeValidFormat() {
    var a = Role.builder().user(stubUser()).role(UserRole.PARTNER).email("bad").build();
    var v = validator.validate(a);
    var cv = one(v, "email");
    assertEquals("{jakarta.validation.constraints.Email.message}", cv.getMessageTemplate());
  }

  @Test
  void emailMax254Chars() {
    var local = "a".repeat(245);
    var email = local + "@example.org";
    assertTrue(email.length() > 254);

    var a = Role.builder().user(stubUser()).role(UserRole.ADMIN).email(email).build();
    var v = validator.validate(a);

    var templates =
        v.stream()
            .filter(cv -> cv.getPropertyPath().toString().equals("email"))
            .map(ConstraintViolation::getMessageTemplate)
            .toList();

    assertTrue(templates.contains("{jakarta.validation.constraints.Size.message}"));
    assertTrue(templates.contains("{jakarta.validation.constraints.Email.message}"));
  }

  private static ConstraintViolation<Role> one(Set<ConstraintViolation<Role>> v, String prop) {
    List<ConstraintViolation<Role>> list =
        v.stream().filter(cv -> cv.getPropertyPath().toString().equals(prop)).toList();
    assertEquals(1, list.size());
    return list.getFirst();
  }
}
