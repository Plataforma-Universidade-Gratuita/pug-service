package br.org.catolicasc.pug.identity.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.AbstractMapperTest;
import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.identity.infra.persistence.AdminEntity;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import com.github.f4b6a3.uuid.UuidCreator;
import org.junit.jupiter.api.DisplayName;

@DisplayName("AdminMapper Tests")
class AdminMapperTest extends AbstractMapperTest<Admin, AdminEntity> {

  @Override
  protected Admin createDomain() {
    return Admin.factory(UuidCreator.getTimeOrderedEpoch(), Campi.JARAGUA_DO_SUL);
  }

  @Override
  protected Admin mapToDomain(AdminEntity entity) {
    return AdminMapper.toDomain(entity);
  }

  @Override
  protected AdminEntity mapToEntity(Admin domain) {
    return AdminMapper.toEntity(domain);
  }

  @Override
  protected void assertRoundTrip(Admin original, Admin mapped) {
    assertThat(mapped).isEqualTo(original);
    assertThat(mapped.getCampus()).isEqualTo(original.getCampus());
  }
}
