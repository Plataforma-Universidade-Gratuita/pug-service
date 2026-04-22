package br.org.catolicasc.pug.partner.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.AbstractMapperTest;
import br.org.catolicasc.pug.identity.infra.persistence.AccountEntity;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.partner.infra.persistence.EntityEntity;
import br.org.catolicasc.pug.partner.infra.persistence.StaffEntity;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("StaffMapper Tests")
class StaffMapperTest extends AbstractMapperTest<Staff, StaffEntity> {

  @Override
  protected Staff createDomain() {
    return Staff.factory(UuidCreator.getTimeOrderedEpoch(), UuidCreator.getTimeOrderedEpoch());
  }

  @Override
  protected Staff mapToDomain(StaffEntity entity) {
    return StaffMapper.toDomain(entity);
  }

  @Override
  protected StaffEntity mapToEntity(Staff domain) {
    return StaffMapper.toEntity(domain);
  }

  @Override
  protected void assertRoundTrip(Staff original, Staff mapped) {
    assertThat(mapped).isEqualTo(original);
  }

  @Test
  @DisplayName("Should project StaffView correctly from entities")
  void shouldProjectToView() {
    AccountEntity acc = new AccountEntity();
    acc.setId(UuidCreator.getTimeOrderedEpoch());
    acc.setUserId(UuidCreator.getTimeOrderedEpoch());
    acc.setEmail("staff@pug.com");
    acc.setAccountType(AccountType.PARTNER);
    acc.setCreatedAt(OffsetDateTime.now());
    acc.setUpdatedAt(OffsetDateTime.now());
    acc.setActive(true);

    EntityEntity ent = new EntityEntity();
    ent.setId(UuidCreator.getTimeOrderedEpoch());
    ent.setCityId(UuidCreator.getTimeOrderedEpoch());

    StaffView view = StaffMapper.toView(acc, ent);

    assertThat(view.account().id()).isEqualTo(acc.getId());
    assertThat(view.entityId()).isEqualTo(ent.getId());
    assertThat(view.cityId()).isEqualTo(ent.getCityId());
  }
}
