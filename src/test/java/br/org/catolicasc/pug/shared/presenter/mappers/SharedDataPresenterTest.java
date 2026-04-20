package br.org.catolicasc.pug.shared.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import br.org.catolicasc.pug.shared.presenter.dtos.CampusResponse;
import java.time.OffsetDateTime;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SharedDataPresenter Tests")
class SharedDataPresenterTest {

  private final I18n i18n = new I18n();

  @Test
  @DisplayName("Should format AuditInfoResponse correctly for pt-BR")
  void shouldFormatAuditInfoPtBr() {
    OffsetDateTime now = OffsetDateTime.now();
    AuditInfoResponse res =
        SharedDataPresenter.createAuditInfoResponse(now, now, Locale.forLanguageTag("pt-BR"));

    assertThat(res).isNotNull();
    assertThat(res.createdAtFormatted()).contains("20", "2026");
  }

  @Test
  @DisplayName("Should translate CampusResponse for pt-BR")
  void shouldTranslateCampus() {
    CampusResponse res =
        SharedDataPresenter.createCampusResponse(
            Campi.JARAGUA_DO_SUL, Locale.forLanguageTag("pt-BR"), i18n);

    assertThat(res).isNotNull();
    assertThat(res.campusFormatted()).isEqualTo("Jaraguá do Sul");
  }

  @Test
  @DisplayName("Should return null if inputs are null")
  void shouldHandleNulls() {
    assertThat(SharedDataPresenter.createAuditInfoResponse(null, null, null)).isNull();
    assertThat(SharedDataPresenter.createCampusResponse(null, null, null)).isNull();
  }
}
