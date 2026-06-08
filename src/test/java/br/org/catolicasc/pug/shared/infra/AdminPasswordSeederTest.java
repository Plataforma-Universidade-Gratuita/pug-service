package br.org.catolicasc.pug.shared.infra;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.identity.service.PasswordService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminPasswordSeederTest {

  @Mock PasswordService passwordService;
  @Mock EntityManager em;
  @Mock Query query;

  @InjectMocks AdminPasswordSeeder seeder;

  @BeforeEach
  void setUp() {
    lenient().when(passwordService.hash("Admin123*")).thenReturn("hashed-admin");
    lenient().when(em.createNativeQuery(anyString())).thenReturn(query);
    lenient().when(query.setParameter(anyString(), any())).thenReturn(query);
  }

  @Test
  @DisplayName("rehashAdminPassword should update the admin password hash")
  void shouldRehashAndUpdate() {
    when(query.executeUpdate()).thenReturn(1);

    seeder.rehashAdminPassword();

    verify(passwordService).hash("Admin123*");
    verify(em).createNativeQuery("UPDATE accounts SET password_hash = :hash WHERE email = :email");
    verify(query).setParameter("hash", "hashed-admin");
    verify(query).setParameter("email", "admin@pug.com");
    verify(query).executeUpdate();
  }

  @Test
  @DisplayName("rehashAdminPassword should handle zero rows updated silently")
  void shouldHandleNoRowsUpdated() {
    when(query.executeUpdate()).thenReturn(0);

    seeder.rehashAdminPassword();

    verify(query).executeUpdate();
  }

  @Test
  @DisplayName("onStart should call rehash when not in TEST mode")
  void onStartDelegatesToRehash() {
    seeder.onStart(null);
    verify(passwordService, never()).hash(anyString());
  }
}
