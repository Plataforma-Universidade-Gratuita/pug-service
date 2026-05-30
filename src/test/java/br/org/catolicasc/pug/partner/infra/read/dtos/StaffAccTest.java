package br.org.catolicasc.pug.partner.infra.read.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("StaffAcc DTO Coverage")
class StaffAccTest {

  @Test
  @DisplayName("Should instantiate StaffAcc projection")
  void shouldInstantiateProjection() {
    StaffAcc projection = new StaffAcc(null, null, null, null);

    assertThat(projection.staff()).isNull();
    assertThat(projection.account()).isNull();
    assertThat(projection.entity()).isNull();
    assertThat(projection.city()).isNull();
  }
}
