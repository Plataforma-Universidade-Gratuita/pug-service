package com.pug.identity.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pug.identity.domain.User;
import com.pug.identity.domain.UsersRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import org.hibernate.search.mapper.orm.Search;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UsersRepositoryImplSearchTest {

  @Inject UsersRepository usersRepository;
  @Inject EntityManager em;

  @BeforeEach
  void setup() throws InterruptedException {
    seedTx();
    Search.session(em).massIndexer(UsersEntity.class).purgeAllOnStart(true).startAndWait();
  }

  @Transactional
  void seedTx() {
    em.createQuery("delete from AdminsEntity").executeUpdate();
    em.createQuery("delete from StaffEntity ").executeUpdate();
    em.createQuery("delete from StudentsEntity ").executeUpdate();
    em.createQuery("delete from UsersEntity").executeUpdate();
    em.persist(
        UsersEntity.builder()
            .cpf("52998224725")
            .name("Jaraguá do Sul")
            .email("u1@example.com")
            .accountType("ADMIN")
            .build());
    em.persist(
        UsersEntity.builder()
            .cpf("16899535009")
            .name("Joinville")
            .email("u2@example.com")
            .accountType("ADMIN")
            .build());
    em.persist(
        UsersEntity.builder()
            .cpf("15350946056")
            .name("Jaragoa do Sul")
            .email("u3@example.com")
            .accountType("ADMIN")
            .build());
    em.persist(
        UsersEntity.builder()
            .cpf("11144477735")
            .name("Araquari")
            .email("u4@example.com")
            .accountType("ADMIN")
            .build());
  }

  @Test
  void listAllUsersReturnsSeeded() {
    var all = usersRepository.listAllUsers();
    assertEquals(4, all.size());
  }

  @Test
  void searchAraOrder() {
    List<String> names = usersRepository.searchByName("ara").stream().map(User::getName).toList();
    assertEquals(3, names.size());
    assertEquals("Araquari", names.get(0));
    assertEquals("Jaragoa do Sul", names.get(1));
    assertEquals("Jaraguá do Sul", names.get(2));
  }

  @Test
  void searchJaraguaOrder() {
    List<String> names =
        usersRepository.searchByName("jaragua").stream().map(User::getName).toList();
    assertEquals(2, names.size());
    assertEquals("Jaraguá do Sul", names.get(0));
    assertEquals("Jaragoa do Sul", names.get(1));
  }

  @Test
  void searchTypoJarguaDoSulOrder() {
    List<String> names =
        usersRepository.searchByName("jargua do sul").stream().map(User::getName).toList();
    assertEquals(2, names.size());
    assertEquals("Jaraguá do Sul", names.get(0));
    assertEquals("Jaragoa do Sul", names.get(1));
  }
}
