package br.org.catolicasc.pug.shared.validation;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("UuidV7 Validation Tests")
class UuidV7Test {

  private Validator validator;

  static class StringDto {
    @UuidV7 String id;

    StringDto(String id) {
      this.id = id;
    }
  }

  static class UuidDto {
    @UuidV7 UUID id;

    UuidDto(UUID id) {
      this.id = id;
    }
  }

  @BeforeEach
  void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Nested
  @DisplayName("Method: isValid (String/UUID)")
  class ValidationTests {

    @Test
    @DisplayName("Should accept valid UUIDv7")
    void shouldAcceptValidV7() {
      UUID v7 = com.github.f4b6a3.uuid.UuidCreator.getTimeOrderedEpoch();

      assertThat(validator.validate(new UuidDto(v7))).isEmpty();
      assertThat(validator.validate(new StringDto(v7.toString()))).isEmpty();
    }

    @Test
    @DisplayName("Should reject invalid UUIDv4")
    void shouldRejectV4() {
      UUID v4 = UUID.randomUUID();

      assertThat(validator.validate(new UuidDto(v4))).hasSize(1);
      assertThat(validator.validate(new StringDto(v4.toString()))).hasSize(1);
    }

    @Test
    @DisplayName("Should reject malformed strings")
    void shouldRejectMalformed() {
      assertThat(validator.validate(new StringDto("not-a-uuid"))).hasSize(1);
    }
  }
}
