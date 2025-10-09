package com.pug.academic.domain.counterpartHours;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CounterpartHoursJpaMappingTest {

  @Inject EntityManager em;

  @Test
  @TestTransaction
  void fkToStudentEnforcedByDb() {
    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                            insert into students_counterparts_hours
                                              (id, student_id, required_hours, start_date, due_date)
                                            values
                                              (gen_random_uuid(), :sid, 10.00, DATE '2025-01-01', DATE '2025-12-31')
                                            """)
              .setParameter("sid", UUID.randomUUID())
              .executeUpdate();
          em.flush();
        });
  }
}
