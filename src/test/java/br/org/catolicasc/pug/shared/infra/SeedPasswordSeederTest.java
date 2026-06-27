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
class SeedPasswordSeederTest {

  @Mock PasswordService passwordService;
  @Mock EntityManager em;
  @Mock Query query;

  @InjectMocks SeedPasswordSeeder seeder;

  @BeforeEach
  void setUp() {
    lenient().when(passwordService.hash("Admin123*")).thenReturn("hashed-admin");
    lenient().when(passwordService.hash("FormerS123*")).thenReturn("hashed-former-student");
    lenient().when(passwordService.hash("EntityS123*")).thenReturn("hashed-entity");

    lenient().when(em.createNativeQuery(anyString())).thenReturn(query);
    lenient().when(query.setParameter(anyString(), any())).thenReturn(query);
  }

  @Test
  @DisplayName("rehashSeedPasswords should update seeded admin, student, and staff passwords")
  void shouldRehashSeedPasswords() {
    when(query.executeUpdate()).thenReturn(3);

    seeder.rehashSeedPasswords();

    verify(passwordService).hash("Admin123*");
    verify(passwordService).hash("FormerS123*");
    verify(passwordService).hash("EntityS123*");

    verify(query).setParameter("emailPattern", "admin.%@pug.test");
    verify(query).setParameter("emailPattern", "student.%@pug.test");
    verify(query).setParameter("emailPattern", "staff.%@pug.test");

    verify(query).setParameter("hash", "hashed-admin");
    verify(query).setParameter("hash", "hashed-former-student");
    verify(query).setParameter("hash", "hashed-entity");

    verify(query, org.mockito.Mockito.times(3)).executeUpdate();
  }

  @Test
  @DisplayName("onStart should not rehash when disabled")
  void onStartShouldNotRehashWhenDisabled() {
    seeder.enabled = false;

    seeder.onStart(null);

    verify(passwordService, never()).hash(anyString());
  }

  @Test
  @DisplayName("onStart should rehash when enabled")
  void onStartShouldRehashWhenEnabled() {
    seeder.enabled = true;
    when(query.executeUpdate()).thenReturn(1);

    seeder.onStart(null);

    verify(passwordService).hash("Admin123*");
    verify(passwordService).hash("FormerS123*");
    verify(passwordService).hash("EntityS123*");
  }
}
