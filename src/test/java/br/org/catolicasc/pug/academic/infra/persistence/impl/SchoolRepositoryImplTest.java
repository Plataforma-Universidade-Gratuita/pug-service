package br.org.catolicasc.pug.academic.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class SchoolRepositoryImplTest {

  @Inject SchoolRepositoryImpl repository;
  @Inject TestDataFactory factory;
  private School areaOfExpertise;

  @BeforeEach
  void setup() {
    areaOfExpertise = factory.createSchool();
  }

  @Test
  @Transactional
  @DisplayName("Should persist and find School")
  void shouldPersistAndFind() {
    Optional<School> found = repository.findOptionalById(areaOfExpertise.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo(areaOfExpertise.getName());
  }
}
