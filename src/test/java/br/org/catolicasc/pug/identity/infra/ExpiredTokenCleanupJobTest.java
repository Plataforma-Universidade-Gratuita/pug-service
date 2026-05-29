package br.org.catolicasc.pug.identity.infra;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.identity.infra.persistence.impl.RefreshTokenRepositoryImpl;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("ExpiredTokenCleanupJob Tests")
class ExpiredTokenCleanupJobTest {

  @Inject ExpiredTokenCleanupJob job;
  @InjectMock RefreshTokenRepositoryImpl refreshTokenRepository;

  @Test
  @DisplayName("Should delete expired tokens when stale rows exist")
  void purgeExpiredTokensWhenRowsExist() {
    when(refreshTokenRepository.deleteExpired()).thenReturn(3L);

    job.purgeExpiredTokens();

    verify(refreshTokenRepository).deleteExpired();
  }

  @Test
  @DisplayName("Should still execute cleanup when no stale rows exist")
  void purgeExpiredTokensWhenNoRowsExist() {
    when(refreshTokenRepository.deleteExpired()).thenReturn(0L);

    job.purgeExpiredTokens();

    verify(refreshTokenRepository).deleteExpired();
  }
}
