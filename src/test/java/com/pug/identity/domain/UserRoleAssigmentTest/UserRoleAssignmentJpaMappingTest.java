package com.pug.identity.domain.UserRoleAssigmentTest;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@QuarkusTest
class UserRoleAssignmentJpaMappingTest {

  @Inject EntityManager em;
}
