package com.pug.identity.domain.UserRoleAssigmentTest;

import com.pug.identity.domain.User;
import com.pug.identity.domain.UserRoleAssignment;
import com.pug.identity.domain.enums.UserRole;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class UserRoleAssignmentJpaMappingTest {

    @Inject
    EntityManager em;

    @Test
    @Transactional
    void persistSetsUuidv7TimestampsAndDefaults() {
        var user = User.builder().cpf("11122233344455").name("Grace Hopper").build();
        em.persist(user);

        var a =
                UserRoleAssignment.builder()
                        .user(user)
                        .role(UserRole.ADMIN)
                        .email("grace@example.com")
                        .build();
        em.persist(a);
        em.flush();
        em.clear();

        var found = em.find(UserRoleAssignment.class, a.getId());
        assertNotNull(found.getId());
        assertEquals(7, found.getId().version());
        assertTrue(found.isActive());
        assertNotNull(found.getCreatedAt());
        assertNotNull(found.getUpdatedAt());
        assertFalse(found.getUpdatedAt().isBefore(found.getCreatedAt()));
        assertEquals(UserRole.ADMIN, found.getRole());
        assertEquals("grace@example.com", found.getEmail());
        assertEquals(user.getId(), found.getUser().getId());
    }

    @Test
    @Transactional
    void emailIsUnique() {
        var user1 = User.builder().cpf("00011122233344").name("A").build();
        var user2 = User.builder().cpf("00011122233345").name("B").build();
        em.persist(user1);
        em.persist(user2);
        em.flush();

        var a1 =
                UserRoleAssignment.builder()
                        .user(user1)
                        .role(UserRole.ADMIN)
                        .email("dup@example.com")
                        .build();
        em.persist(a1);
        em.flush();

        var a2 =
                UserRoleAssignment.builder()
                        .user(user2)
                        .role(UserRole.FORMER_STUDENT)
                        .email("dup@example.com")
                        .build();
        em.persist(a2);
        assertThrows(PersistenceException.class, em::flush);
    }

    @Test
    @Transactional
    void emailLengthEnforcedByDb_evenIfValidationBypassed() {
        var user = User.builder().cpf("55544433322211").name("Alan Turing").build();
        em.persist(user);
        em.flush();

        assertThrows(
                PersistenceException.class,
                () -> {
                    em.createNativeQuery(
                                    """
                                            insert into users_roles (id, user_id, role, email, active, created_at, updated_at)
                                            values (gen_random_uuid(), :uid, :role, :email, true, now(), now())
                                            """)
                            .setParameter("uid", user.getId())
                            .setParameter("role", UserRole.ADMIN.name())
                            .setParameter("email", "x".repeat(255))
                            .executeUpdate();
                    em.flush();
                });
    }
}
