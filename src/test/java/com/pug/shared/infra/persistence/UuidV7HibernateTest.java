package com.pug.shared.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

public class UuidV7HibernateTest {

  @Test
  public void testGenerateUuid() {
    UuidV7Hibernate uuidGenerator = new UuidV7Hibernate();

    UUID uuid = uuidGenerator.generateUuid(null);

    assertNotNull(uuid, "Generated UUID should not be null");
    assertTrue(isValidUuidFormat(uuid), "Generated UUID should have a valid format");
  }

  private boolean isValidUuidFormat(UUID uuid) {
    String uuidString = uuid.toString();
    return uuidString.matches(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
  }
}
