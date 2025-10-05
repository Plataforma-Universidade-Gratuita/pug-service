package com.pug.identity.domain.UserRoleAssigmentTest;

import com.pug.identity.domain.User;
import com.pug.identity.domain.UserRoleAssignment;
import com.pug.identity.domain.enums.UserRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRoleAssignmentValidationTest {
    static Validator validator;
    static Locale original;

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
    void validAssignmentPasses() {
        var u = User.builder().cpf("12345678901234").name("Ada Lovelace").build();
        var a =
                UserRoleAssignment.builder()
                        .user(u)
                        .role(UserRole.ADMIN)
                        .email("ada@example.com")
                        .build();
        var v = validator.validate(a);
        assertTrue(v.isEmpty());
    }

    @Test
    void userAndRoleAreRequired() {
        var a = UserRoleAssignment.builder().email("ada@example.com").build();
        var v = validator.validate(a);
        assertEquals("{jakarta.validation.constraints.NotNull.message}", one(v, "user").getMessageTemplate());
        assertEquals("{jakarta.validation.constraints.NotNull.message}", one(v, "role").getMessageTemplate());
    }

    @Test
    void emailCannotBeBlank() {
        var u = User.builder().cpf("12345678901234").name("Ada").build();
        var blank = UserRoleAssignment.builder().user(u).role(UserRole.ADMIN).email(" ").build();
        var v = validator.validate(blank);

        var msgs = v.stream()
                .filter(cv -> cv.getPropertyPath().toString().equals("email"))
                .map(ConstraintViolation::getMessageTemplate)
                .toList();

        assertEquals(2, msgs.size());
        assertTrue(msgs.contains("{jakarta.validation.constraints.NotBlank.message}"));
        assertTrue(msgs.contains("{jakarta.validation.constraints.Email.message}"));
    }


    private static ConstraintViolation<UserRoleAssignment> one(Set<ConstraintViolation<UserRoleAssignment>> v, String prop) {
        List<ConstraintViolation<UserRoleAssignment>> list =
                v.stream().filter(cv -> cv.getPropertyPath().toString().equals(prop)).toList();
        assertEquals(1, list.size());
        return list.getFirst();
    }
}
