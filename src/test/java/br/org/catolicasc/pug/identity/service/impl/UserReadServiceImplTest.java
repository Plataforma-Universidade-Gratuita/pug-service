package br.org.catolicasc.pug.identity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.identity.infra.read.UserQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.UserView;
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
@DisplayName("UserReadServiceImpl Tests")
class UserReadServiceImplTest {

  @Mock UserQueries queries;
  @InjectMocks UserReadServiceImpl service;

  @Nested
  @DisplayName("Method: getViewById")
  class GetViewByIdTests {
    @Test
    @DisplayName("Should return view when user exists")
    void shouldReturnView() {
      UUID id = UUID.randomUUID();
      UserView view = new UserView(id, "11144477735", "Name", null, null);
      when(queries.findOptionalById(id)).thenReturn(Optional.of(view));

      assertThat(service.getViewById(id)).isEqualTo(view);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user missing")
    void shouldThrowException() {
      when(queries.findOptionalById(any())).thenReturn(Optional.empty());
      org.junit.jupiter.api.Assertions.assertThrows(
          ResourceNotFoundException.class, () -> service.getViewById(UUID.randomUUID()));
    }
  }
}
