package br.org.catolicasc.pug.shared.infra.audit;

import br.org.catolicasc.pug.identity.presenter.dtos.auth.LoginRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.TokenResponse;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import java.util.UUID;

@ApplicationScoped
@Alternative
@Priority(1)
public class MockAuthService implements AuthService {

  public static final UUID TEST_ACCOUNT_ID = UUID.randomUUID();

  @Override
  public UUID getCurrentAccountId() {
    return TEST_ACCOUNT_ID;
  }

  @Override
  public UUID getCurrentUserId() {
    return UUID.randomUUID();
  }

  @Override
  public AccountType getCurrentAccountType() {
    return AccountType.ADMIN;
  }

  @Override
  public TokenResponse login(LoginRequest request) {
    return null;
  }

  @Override
  public void requireCurrentAccountNotOfType(AccountType forbidden) {}

  @Override
  public void requireCurrentAccountOfType(AccountType allowed) {}
}
