package br.org.catolicasc.pug.identity.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.identity.infra.read.dtos.AccountComplexSearchView;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.identity.presenter.dtos.AccountComplexSearchResponse;
import br.org.catolicasc.pug.identity.presenter.dtos.AccountResponse;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.i18n.I18n;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AccountPresenter Coverage")
class AccountPresenterTest {

  @Inject I18n i18n;

  @Nested
  @DisplayName("Mapping Logic Tests")
  class MappingTests {

    @Test
    @DisplayName("Should return null when input view is null")
    void shouldReturnNullOnViewNull() {
      assertThat(AccountPresenter.toResponse(null, Locale.US, i18n)).isNull();
    }

    @Test
    @DisplayName("Should return null when locale is null")
    void shouldReturnNullOnLocaleNull() {
      AccountView view =
          new AccountView(
              UuidCreator.getTimeOrderedEpoch(),
              UuidCreator.getTimeOrderedEpoch(),
              "test@pug.com",
              AccountType.STUDENT,
              null,
              null,
              true);
      assertThat(AccountPresenter.toResponse(view, null, i18n)).isNull();
    }

    @Test
    @DisplayName("Should map AccountView to AccountResponse correctly")
    void shouldMapSuccessfully() {
      UUID id = UuidCreator.getTimeOrderedEpoch();
      UUID userId = UuidCreator.getTimeOrderedEpoch();
      OffsetDateTime now = OffsetDateTime.now();
      AccountView view =
          new AccountView(id, userId, "test@pug.com", AccountType.STUDENT, now, now, true);

      AccountResponse response = AccountPresenter.toResponse(view, Locale.US, i18n);

      assertThat(response).isNotNull();
      assertThat(response.id()).isEqualTo(id);
      assertThat(response.userId()).isEqualTo(userId);
      assertThat(response.email()).isEqualTo("test@pug.com");
      assertThat(response.accountType()).isEqualTo(AccountType.STUDENT);
      assertThat(response.accountTypeFormatted())
          .isEqualTo(i18n.translation(AccountType.STUDENT.getBundleKey(), Locale.US));
      assertThat(response.active()).isTrue();
      assertThat(response.auditInfo()).isNotNull();
    }

    @Test
    @DisplayName("Should map AccountComplexSearchView to AccountComplexSearchResponse correctly")
    void shouldMapComplexSearchSuccessfully() {
      UUID id = UuidCreator.getTimeOrderedEpoch();
      UUID userId = UuidCreator.getTimeOrderedEpoch();
      OffsetDateTime now = OffsetDateTime.now();
      AccountComplexSearchView view =
          new AccountComplexSearchView(
              id, userId, "Test User", "test@pug.com", AccountType.STUDENT, now, now, true);

      AccountComplexSearchResponse response =
          AccountPresenter.toComplexSearchResponse(view, Locale.US, i18n);

      assertThat(response).isNotNull();
      assertThat(response.id()).isEqualTo(id);
      assertThat(response.user().id()).isEqualTo(userId);
      assertThat(response.user().name()).isEqualTo("Test User");
      assertThat(response.email()).isEqualTo("test@pug.com");
      assertThat(response.accountType()).isEqualTo(AccountType.STUDENT);
      assertThat(response.accountTypeFormatted())
          .isEqualTo(i18n.translation(AccountType.STUDENT.getBundleKey(), Locale.US));
      assertThat(response.active()).isTrue();
      assertThat(response.auditInfo()).isNotNull();
    }
  }
}
