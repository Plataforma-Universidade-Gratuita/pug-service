package br.org.catolicasc.pug.partner.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.partner.infra.persistence.StaffEntity;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("StaffMapper Tests")
class StaffMapperTest {

  @Test
  @DisplayName("Should perform round-trip mapping for Staff")
  void shouldPerformRoundTrip() {
    Staff staff = Staff.factory(UUID.randomUUID(), UUID.randomUUID());

    StaffEntity persistence = StaffMapper.toEntity(staff);
    Staff mapped = StaffMapper.toDomain(persistence);

    assertThat(mapped.getAccountId()).isEqualTo(staff.getAccountId());
    assertThat(mapped.getEntityId()).isEqualTo(staff.getEntityId());
  }
}
