package br.org.catolicasc.pug.identity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.identity.infra.read.AdminQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.AdminView;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminReadServiceImpl Coverage Tests")
class AdminReadServiceImplTest {

  @Mock AdminQueries queries;
  @InjectMocks AdminReadServiceImpl service;

  @Nested
  @DisplayName("Method: getViewByAccountId")
  class GetByAccountIdTests {
    @Test
    @DisplayName("Should return admin view successfully")
    void success() {
      UUID id = UUID.randomUUID();
      AdminView view = new AdminView(null, null, Campi.JARAGUA_DO_SUL);
      when(queries.findOptionalById(id)).thenReturn(Optional.of(view));

      assertThat(service.getViewByAccountId(id)).isEqualTo(view);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when missing")
    void notFound() {
      when(queries.findOptionalById(any())).thenReturn(Optional.empty());
      org.junit.jupiter.api.Assertions.assertThrows(
          ResourceNotFoundException.class, () -> service.getViewByAccountId(UUID.randomUUID()));
    }
  }

  @Nested
  @DisplayName("Method: search")
  class SearchTests {
    @Test
    @DisplayName("Should normalize search query")
    void searchNormalization() {
      service.search(" Joinville ");
      verify(queries).searchByName("joinville");
    }
  }
}
