package br.org.catolicasc.pug.identity.infra;

import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.identity.infra.persistence.AdminEntity;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AdminMapper Tests")
class AdminMapperTest {

    @Test
    @DisplayName("Should perform round-trip mapping for Admin")
    void shouldPerformRoundTrip() {
        Admin admin = Admin.factory(UUID.randomUUID(), Campi.JARAGUA_DO_SUL);

        AdminEntity entity = AdminMapper.toEntity(admin);
        Admin mappedAdmin = AdminMapper.toDomain(entity);

        assertThat(mappedAdmin).isEqualTo(admin);
        assertThat(mappedAdmin.getCampus()).isEqualTo(admin.getCampus());
    }
}