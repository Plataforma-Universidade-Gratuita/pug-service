package br.org.catolicasc.pug.partner.infra.read.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Partner Read DTOs")
class PartnerReadDtosTest {

  @Test
  @DisplayName("Should expose EntityView values correctly")
  void entityView() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    UUID cityId = UuidCreator.getTimeOrderedEpoch();
    OffsetDateTime now = OffsetDateTime.now();

    EntityView view = new EntityView(id, "12345678000199", "Entity", "Street 1", cityId, now, now);

    assertThat(view.id()).isEqualTo(id);
    assertThat(view.cnpj()).isEqualTo("12345678000199");
    assertThat(view.name()).isEqualTo("Entity");
    assertThat(view.address()).isEqualTo("Street 1");
    assertThat(view.cityId()).isEqualTo(cityId);
    assertThat(view.createdAt()).isEqualTo(now);
    assertThat(view.updatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("Should expose StaffAcc values correctly")
  void staffAcc() {
    StaffAcc acc = new StaffAcc(null, null, null, null);

    assertThat(acc.staff()).isNull();
    assertThat(acc.account()).isNull();
    assertThat(acc.entity()).isNull();
    assertThat(acc.city()).isNull();
  }

  @Test
  @DisplayName("Should expose StaffView values correctly")
  void staffView() {
    OffsetDateTime now = OffsetDateTime.now();
    AccountView account =
        new AccountView(
            UuidCreator.getTimeOrderedEpoch(),
            UuidCreator.getTimeOrderedEpoch(),
            "user@example.com",
            AccountType.PARTNER,
            now,
            now,
            true);
    UUID entityId = UuidCreator.getTimeOrderedEpoch();
    UUID cityId = UuidCreator.getTimeOrderedEpoch();

    StaffView view = new StaffView(account, entityId, cityId);

    assertThat(view.account()).isEqualTo(account);
    assertThat(view.entityId()).isEqualTo(entityId);
    assertThat(view.cityId()).isEqualTo(cityId);
  }
}
