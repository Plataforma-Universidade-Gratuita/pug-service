package br.org.catolicasc.pug.identity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.identity.infra.read.AccountQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import java.util.List;
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
@DisplayName("AccountReadServiceImpl Coverage Tests")
class AccountReadServiceImplTest {

  @Mock AccountQueries queries;
  @InjectMocks AccountReadServiceImpl service;

  @Nested
  @DisplayName("Method: getViewById")
  class GetByIdTests {
    @Test
    @DisplayName("Should return view when account exists")
    void success() {
      UUID id = UUID.randomUUID();
      AccountView view =
          new AccountView(id, UUID.randomUUID(), "test@pug.com", null, null, null, true);
      when(queries.findOptionalById(id)).thenReturn(Optional.of(view));

      assertThat(service.getViewById(id)).isEqualTo(view);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when missing")
    void notFound() {
      when(queries.findOptionalById(any())).thenReturn(Optional.empty());
      org.junit.jupiter.api.Assertions.assertThrows(
          ResourceNotFoundException.class, () -> service.getViewById(UUID.randomUUID()));
    }
  }

  @Nested
  @DisplayName("Method: search")
  class SearchTests {
    @Test
    @DisplayName("Should fold query and call searchByName")
    void shouldFoldAndSearch() {
      when(queries.searchByName("sao paulo")).thenReturn(List.of());

      service.search("  São Paulo  ");

      verify(queries).searchByName("sao paulo");
    }
  }
}
