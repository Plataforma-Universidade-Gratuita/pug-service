package com.pug.academic.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.FieldOfStudy;
import com.pug.academic.domain.Student;
import com.pug.identity.domain.Role;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.UserRole;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StudentRepositoryTest {

  @Inject StudentRepository repo;
  @Inject EntityManager em;

  @Test
  @TestTransaction
  void existsByAcademicRegistrationTrueWhenPresentFalseWhenAbsent() {
    var deps = seedDeps("engineering", "databases");
    persistStudent(deps.role(), "AR100", deps.course());

    assertTrue(repo.existsByAcademicRegistration("AR100"));
    assertFalse(repo.existsByAcademicRegistration("AR200"));
  }

  @Test
  @TestTransaction
  void existsByAcademicRegistrationForAnotherIgnoresSameChecksOthers() {
    var deps = seedDeps("computing", "algorithms");
    var a = persistStudent(deps.role(), "AR1", deps.course());
    var b = persistStudent(seedDeps("computing2", "os").role(), "AR2", deps.course());

    assertFalse(repo.existsByAcademicRegistrationForAnother("AR1", a.getId()));
    assertTrue(repo.existsByAcademicRegistrationForAnother("AR1", b.getId()));
  }

  @Test
  @TestTransaction
  void findByAcademicRegistrationPresentAndEmpty() {
    var deps = seedDeps("law", "civil law i");
    persistStudent(deps.role(), "REG123", deps.course());

    assertTrue(repo.findByAcademicRegistration("REG123").isPresent());
    assertTrue(repo.findByAcademicRegistration("REG999").isEmpty());
  }

  @Test
  @TestTransaction
  void findByUserRoleIdPresentAndEmpty() {
    var deps = seedDeps("engineering", "networks");
    var s = persistStudent(deps.role(), "AR77", deps.course());

    assertEquals(Optional.of(s), repo.findByUserRoleId(deps.role().getId()));
    assertTrue(repo.findByUserRoleId(UUID.randomUUID()).isEmpty());
  }

  private record Deps(Role role, Course course) {}

  private Deps seedDeps(String fieldName, String courseName) {
    var fos = FieldOfStudy.builder().name(fieldName).build();
    em.persist(fos);

    var course = Course.builder().name(courseName).field(fos).build();
    em.persist(course);

    var user = User.builder().cpf(uniqueCpf()).name("Ada").build();
    em.persist(user);

    var role = Role.builder().user(user).email(uniqueEmail()).role(UserRole.FORMER_STUDENT).build();
    em.persist(role);

    em.flush();
    return new Deps(role, course);
  }

  private Student persistStudent(Role role, String reg, Course course) {
    var s = Student.builder().userRole(role).academicRegistration(reg).course(course).build();
    em.persist(s);
    em.flush();
    return s;
  }

  private String uniqueEmail() {
    return "u" + UUID.randomUUID().toString().replace("-", "").substring(0, 10) + "@test.org";
  }

  private String uniqueCpf() {
    java.util.concurrent.ThreadLocalRandom r = java.util.concurrent.ThreadLocalRandom.current();

    int[] n = new int[11];
    while (true) {
      for (int i = 0; i < 9; i++) n[i] = r.nextInt(10);
      // avoid all-equal sequences
      boolean allEqual = true;
      for (int i = 1; i < 9; i++)
        if (n[i] != n[0]) {
          allEqual = false;
          break;
        }
      if (allEqual) continue;

      // d1
      int s1 = 0;
      for (int i = 0, w = 10; i < 9; i++, w--) s1 += n[i] * w;
      int d1 = 11 - (s1 % 11);
      if (d1 >= 10) d1 = 0;
      n[9] = d1;

      // d2
      int s2 = 0;
      for (int i = 0, w = 11; i < 10; i++, w--) s2 += n[i] * w;
      int d2 = 11 - (s2 % 11);
      if (d2 >= 10) d2 = 0;
      n[10] = d2;

      StringBuilder sb = new StringBuilder(11);
      for (int d : n) sb.append(d);
      return sb.toString();
    }
  }
}
