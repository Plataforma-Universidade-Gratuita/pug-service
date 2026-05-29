package br.org.catolicasc.pug.identity.presenter.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.UriInfo;
import java.security.Principal;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PasswordSetupGuardFilter Coverage")
class PasswordSetupGuardFilterTest {

  @Test
  @DisplayName("Should ignore anonymous requests")
  void shouldIgnoreAnonymousRequests() {
    PasswordSetupGuardFilter filter = new PasswordSetupGuardFilter();
    SecurityIdentity identity = mock(SecurityIdentity.class);
    when(identity.isAnonymous()).thenReturn(true);
    filter.identity = identity;

    assertDoesNotThrow(() -> filter.filter(mockRequestContext("v1/identity/users")));
  }

  @Test
  @DisplayName("Should allow auth endpoints even when password is not wired")
  void shouldAllowAuthEndpoints() {
    PasswordSetupGuardFilter filter = new PasswordSetupGuardFilter();
    SecurityIdentity identity = mock(SecurityIdentity.class);
    JsonWebToken jwt = mock(JsonWebToken.class);

    when(identity.isAnonymous()).thenReturn(false);
    when(identity.getPrincipal()).thenReturn(jwt);
    when(jwt.getClaim("passwordWired")).thenReturn(false);
    filter.identity = identity;

    assertDoesNotThrow(() -> filter.filter(mockRequestContext("v1/auth/wire-credentials")));
  }

  @Test
  @DisplayName("Should ignore non-JWT authenticated principals")
  void shouldIgnoreNonJwtPrincipals() {
    PasswordSetupGuardFilter filter = new PasswordSetupGuardFilter();
    SecurityIdentity identity = mock(SecurityIdentity.class);
    Principal principal = mock(Principal.class);

    when(identity.isAnonymous()).thenReturn(false);
    when(identity.getPrincipal()).thenReturn(principal);
    filter.identity = identity;

    assertDoesNotThrow(() -> filter.filter(mockRequestContext("v1/identity/users")));
  }

  @Test
  @DisplayName("Should block protected endpoints when password is not wired")
  void shouldBlockProtectedEndpoints() {
    PasswordSetupGuardFilter filter = new PasswordSetupGuardFilter();
    SecurityIdentity identity = mock(SecurityIdentity.class);
    JsonWebToken jwt = mock(JsonWebToken.class);

    when(identity.isAnonymous()).thenReturn(false);
    when(identity.getPrincipal()).thenReturn(jwt);
    when(jwt.getClaim("passwordWired")).thenReturn(false);
    filter.identity = identity;

    assertThrows(
        BusinessRuleException.class, () -> filter.filter(mockRequestContext("v1/identity/users")));
  }

  @Test
  @DisplayName("Should allow protected endpoints when password is already wired")
  void shouldAllowProtectedEndpoints() {
    PasswordSetupGuardFilter filter = new PasswordSetupGuardFilter();
    SecurityIdentity identity = mock(SecurityIdentity.class);
    JsonWebToken jwt = mock(JsonWebToken.class);

    when(identity.isAnonymous()).thenReturn(false);
    when(identity.getPrincipal()).thenReturn(jwt);
    when(jwt.getClaim("passwordWired")).thenReturn(true);
    filter.identity = identity;

    assertDoesNotThrow(() -> filter.filter(mockRequestContext("v1/identity/users")));
  }

  private static ContainerRequestContext mockRequestContext(String path) {
    ContainerRequestContext context = mock(ContainerRequestContext.class);
    UriInfo uriInfo = mock(UriInfo.class);
    when(context.getUriInfo()).thenReturn(uriInfo);
    when(uriInfo.getPath()).thenReturn(path);
    return context;
  }
}
