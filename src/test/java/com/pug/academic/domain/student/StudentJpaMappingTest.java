package com.pug.academic.domain.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StudentJpaMappingTest {

  @Inject EntityManager em;

  @Test
  @TestTransaction
  void persistLoadsBackAndUniquenessEnforced() {
    var fos = FieldOfStudy.builder().name("engineering").build();
    em.persist(fos);
    var course = Course.builder().name("databases").field(fos).build();
    em.persist(course);

    var u = User.builder().cpf("93541134780").name("Ada").build();
    em.persist(u);
    var role =
        Role.builder().user(u).email("ada@example.org").role(UserRole.FORMER_STUDENT).build();
    em.persist(role);

    var s = Student.builder().userRole(role).academicRegistration("AR123").course(course).build();
    em.persist(s);
    em.flush();
    em.clear();

    UUID id = s.getId();
    assertNotNull(id);
    var found = em.find(Student.class, id);
    assertNotNull(found);
    assertEquals("AR123", found.getAcademicRegistration());
    assertEquals(course.getId(), found.getCourse().getId());
    assertEquals(role.getId(), found.getUserRole().getId());

    var u2 = User.builder().cpf("39053344705").name("Grace").build();
    em.persist(u2);
    var role2 =
        Role.builder().user(u2).email("grace@example.org").role(UserRole.FORMER_STUDENT).build();
    em.persist(role2);

    var dupReg =
        Student.builder().userRole(role2).academicRegistration("AR123").course(course).build();
    em.persist(dupReg);
    assertThrows(PersistenceException.class, em::flush);

    em.clear();

    var anotherStudent =
        Student.builder().userRole(role).academicRegistration("AR124").course(course).build();
    em.persist(anotherStudent);
    assertThrows(PersistenceException.class, em::flush);
  }

  @Test
  @TestTransaction
  void notNullAssociationsValidatedOnFlush() {
    var s = Student.builder().userRole(null).academicRegistration("ARX").course(null).build();
    em.persist(s);
    assertThrows(ConstraintViolationException.class, em::flush);
  }
}
