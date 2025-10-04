package com.pug.identity.infra.persistence;

import com.pug.identity.domain.UserRoleAssignment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRoleAssignmentRepository implements PanacheRepository<UserRoleAssignment> {}
