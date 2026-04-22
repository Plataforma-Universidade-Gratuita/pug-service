package br.org.catolicasc.pug.partner.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.identity.infra.persistence.AccountEntity;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.partner.infra.persistence.EntityEntity;
import br.org.catolicasc.pug.partner.infra.persistence.StaffEntity;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import java.time.OffsetDateTime;
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

    assertThat(mapped).isEqualTo(staff);
  }

  @Test
  @DisplayName("Should project StaffView correctly from entities")
  void shouldProjectToView() {
    AccountEntity acc = new AccountEntity();
    acc.setId(UUID.randomUUID());
    acc.setUserId(UUID.randomUUID());
    acc.setEmail("staff@pug.com");
    acc.setAccountType(AccountType.PARTNER);
    acc.setCreatedAt(OffsetDateTime.now());
    acc.setUpdatedAt(OffsetDateTime.now());
    acc.setActive(true);

    EntityEntity ent = new EntityEntity();
    ent.setId(UUID.randomUUID());
    ent.setCityId(UUID.randomUUID());

    StaffView view = StaffMapper.toView(acc, ent);

    assertThat(view.account().id()).isEqualTo(acc.getId());
    assertThat(view.entityId()).isEqualTo(ent.getId());
    assertThat(view.cityId()).isEqualTo(ent.getCityId());
  }

  @Test
  @DisplayName("Should return null on null inputs")
  void shouldReturnNullOnNull() {
    assertThat(StaffMapper.toDomain(null)).isNull();
    assertThat(StaffMapper.toEntity(null)).isNull();
  }
}
