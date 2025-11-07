package com.pug.identity.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.AppValidationException;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

public class UserTest {

  @Test
  void build_valid_user() {
    User u =
        User.builder()
            .id(null)
            .cpf(new Cpf("52998224725"))
            .name("Alice Test")
            .email(new Email("Alice.Test@Example.com"))
            .accountType(AccountType.STUDENT)
            .passwordHash(null)
            .active(Boolean.TRUE)
            .createdAt(null)
            .build();

    assertEquals("alice.test@example.com", u.getEmail().toString());
    assertEquals("52998224725", u.getCpf().toString());
    assertTrue(u.getActive());
  }

  @Test
  void null_cpf_throws() {
    assertThrows(
        AppValidationException.class,
        () ->
            User.builder()
                .cpf(null)
                .name("N")
                .email(new Email("a@b.com"))
                .accountType(AccountType.ADMIN)
                .active(Boolean.TRUE)
                .build());
  }

  @Test
  void null_email_throws() {
    assertThrows(
        AppValidationException.class,
        () ->
            User.builder()
                .cpf(new Cpf("52998224725"))
                .name("N")
                .email(null)
                .accountType(AccountType.ADMIN)
                .active(Boolean.TRUE)
                .build());
  }

  @Test
  void blank_name_throws() {
    assertThrows(
        AppValidationException.class,
        () ->
            User.builder()
                .cpf(new Cpf("52998224725"))
                .name(" ")
                .email(new Email("a@b.com"))
                .accountType(AccountType.ADMIN)
                .active(Boolean.TRUE)
                .build());
  }

  @Test
  void too_long_name_throws() {
    String longName = "a".repeat(151);
    assertThrows(
        AppValidationException.class,
        () ->
            User.builder()
                .cpf(new Cpf("52998224725"))
                .name(longName)
                .email(new Email("a@b.com"))
                .accountType(AccountType.ADMIN)
                .active(Boolean.TRUE)
                .build());
  }

  @Test
  void null_account_type_throws() {
    assertThrows(
        AppValidationException.class,
        () ->
            User.builder()
                .cpf(new Cpf("52998224725"))
                .name("N")
                .email(new Email("a@b.com"))
                .accountType(null)
                .active(Boolean.TRUE)
                .build());
  }

  @Test
  void too_long_password_hash_throws() {
    String longHash = "x".repeat(256);
    assertThrows(
        AppValidationException.class,
        () ->
            User.builder()
                .cpf(new Cpf("52998224725"))
                .name("N")
                .email(new Email("a@b.com"))
                .accountType(AccountType.ADMIN)
                .passwordHash(longHash)
                .active(Boolean.TRUE)
                .build());
  }

  @Test
  void null_active_throws() {
    assertThrows(
        AppValidationException.class,
        () ->
            User.builder()
                .cpf(new Cpf("52998224725"))
                .name("N")
                .email(new Email("a@b.com"))
                .accountType(AccountType.ADMIN)
                .active(null)
                .build());
  }

  @Test
  void created_at_in_future_throws() {
    assertThrows(
        AppValidationException.class,
        () ->
            User.builder()
                .cpf(new Cpf("52998224725"))
                .name("N")
                .email(new Email("a@b.com"))
                .accountType(AccountType.ADMIN)
                .active(Boolean.TRUE)
                .createdAt(OffsetDateTime.now().plusMinutes(1))
                .build());
  }
}
