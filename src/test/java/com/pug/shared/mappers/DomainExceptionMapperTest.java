package com.pug.shared.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pug.shared.errors.DomainException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DomainExceptionMapperTest {

  private static final String USER_NOT_FOUND = "USER_NOT_FOUND";
  private static final String ROLE_NOT_FOUND = "ROLE_NOT_FOUND";
  private static final String ENTITY_NOT_FOUND = "ENTITY_NOT_FOUND";
  private static final String CITY_NOT_FOUND = "CITY_NOT_FOUND";
  private static final String STAFF_NOT_FOUND = "STAFF_NOT_FOUND";
  private static final String STAFF_DUPLICATE_USER_ROLE_ID = "STAFF_DUPLICATE_USER_ROLE_ID";
  private static final String USER_DUPLICATE_CPF = "USER_DUPLICATE_CPF";
  private static final String ROLE_DUPLICATE_EMAIL = "ROLE_DUPLICATE_EMAIL";
  private static final String ENTITY_DUPLICATE_CNPJ = "ENTITY_DUPLICATE_CNPJ";
  private static final String USER_ALREADY_REGISTERED_AS_FORMER_STUDENT =
      "USER_ALREADY_REGISTERED_AS_FORMER_STUDENT";
  private static final String FIELD_OF_STUDY_DUPLICATE_NAME = "FIELD_OF_STUDY_DUPLICATE_NAME";

  private static DomainException ex(String code, Object... args) {
    return new DomainException(code, args) {};
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> details(DomainExceptionMapper mapper, DomainException ex)
      throws Exception {
    Method m = DomainExceptionMapper.class.getDeclaredMethod("buildDetails", DomainException.class);
    m.setAccessible(true);
    return (Map<String, Object>) m.invoke(mapper, ex);
  }

  @Test
  void buildDetailsAllBranchesCovered() throws Exception {
    var mapper = new DomainExceptionMapper();

    var uid = UUID.randomUUID();
    var rid = UUID.randomUUID();
    var eid = UUID.randomUUID();

    // USER_NOT_FOUND
    assertEquals(Map.of("id", uid), details(mapper, ex(USER_NOT_FOUND, uid)));
    assertEquals(Map.of("cpf", "93541134780"), details(mapper, ex(USER_NOT_FOUND, "93541134780")));

    // ROLE_NOT_FOUND
    assertEquals(Map.of("id", rid), details(mapper, ex(ROLE_NOT_FOUND, rid)));
    assertEquals(Map.of("email", "a@b.org"), details(mapper, ex(ROLE_NOT_FOUND, "a@b.org")));

    // ENTITY_NOT_FOUND
    assertEquals(Map.of("id", eid), details(mapper, ex(ENTITY_NOT_FOUND, eid)));
    assertEquals(
        Map.of("cnpj", "11222333000181"), details(mapper, ex(ENTITY_NOT_FOUND, "11222333000181")));

    // CITY_NOT_FOUND
    assertEquals(Map.of("ibgeCode", "4205407"), details(mapper, ex(CITY_NOT_FOUND, "4205407")));

    // STAFF_NOT_FOUND / STAFF_DUPLICATE_USER_ROLE_ID
    assertEquals(Map.of("userRoleId", uid), details(mapper, ex(STAFF_NOT_FOUND, uid)));
    assertEquals(Map.of("userRoleId", uid), details(mapper, ex(STAFF_DUPLICATE_USER_ROLE_ID, uid)));

    // USER_DUPLICATE_CPF
    assertEquals(
        Map.of("cpf", "93541134780"), details(mapper, ex(USER_DUPLICATE_CPF, "93541134780")));

    // ROLE_DUPLICATE_EMAIL
    assertEquals(
        Map.of("email", "dup@example.org"),
        details(mapper, ex(ROLE_DUPLICATE_EMAIL, "dup@example.org")));

    // ENTITY_DUPLICATE_CNPJ
    assertEquals(
        Map.of("cnpj", "11222333000181"),
        details(mapper, ex(ENTITY_DUPLICATE_CNPJ, "11222333000181")));

    // FIELD_OF_STUDY_DUPLICATE_NAME
    assertEquals(Map.of("name", "Law"), details(mapper, ex(FIELD_OF_STUDY_DUPLICATE_NAME, "Law")));

    // USER_ALREADY_REGISTERED_AS_FORMER_STUDENT
    assertEquals(
        Map.of("user", uid), details(mapper, ex(USER_ALREADY_REGISTERED_AS_FORMER_STUDENT, uid)));

    // default branch with single arg -> {"arg": a}
    assertEquals(Map.of("arg", "x"), details(mapper, ex("SOME_OTHER_CODE", "x")));

    // args length != 1 -> empty
    assertEquals(Map.of(), details(mapper, ex(USER_NOT_FOUND, "a", "b")));
  }
}
