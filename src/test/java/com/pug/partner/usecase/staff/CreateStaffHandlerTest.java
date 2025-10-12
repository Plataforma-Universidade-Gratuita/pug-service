package com.pug.partner.usecase.staff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.identity.domain.Role;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.Staff;
import com.pug.partner.domain.exceptions.DuplicateStaffException;
import com.pug.partner.infra.persistence.StaffRepository;
import com.pug.partner.usecase.staff.create.CreateStaffCommand;
import com.pug.partner.usecase.staff.create.CreateStaffHandler;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateStaffHandlerTest {

  @Mock StaffRepository repo;
  @Mock EntityManager em;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks
  CreateStaffHandler handler;

  private static Role roleRef(UUID id) {
    return Role.builder().id(id).build();
  }

  private static PartnerEntity entityRef(UUID id) {
    return PartnerEntity.builder().id(id).build();
  }

  @Test
  void createSuccess_persists_flushes_and_returnsStaff() {
    var userRoleId = UUID.randomUUID();
    var entityId = UUID.randomUUID();
    var fixed = UUID.fromString("00000000-0000-7000-8000-000000000123");

    when(repo.findByUserRoleId(userRoleId)).thenReturn(Optional.empty());
    when(em.getReference(Role.class, userRoleId)).thenReturn(roleRef(userRoleId));
    when(em.getReference(PartnerEntity.class, entityId)).thenReturn(entityRef(entityId));
    Mockito.doAnswer(
            inv -> {
              Staff s = inv.getArgument(0);
              s.setId(fixed);
              return null;
            })
        .when(repo)
        .persist(any(Staff.class));

    var out = handler.handle(new CreateStaffCommand(userRoleId, entityId));

    assertNotNull(out);
    assertEquals(fixed, out.getId());
    assertEquals(userRoleId, out.getUserRole().getId());
    assertEquals(entityId, out.getEntity().getId());

    InOrder io = inOrder(repo, em);
    io.verify(repo).findByUserRoleId(userRoleId);
    io.verify(em).getReference(Role.class, userRoleId);
    io.verify(em).getReference(PartnerEntity.class, entityId);
    io.verify(repo).persist(any(Staff.class));
    io.verify(repo).flush();
    verifyNoMoreInteractions(repo, em);
  }

  @Test
  void duplicateUserRole_fastPathThrows_andSkipsPersist() {
    var userRoleId = UUID.randomUUID();
    var entityId = UUID.randomUUID();
    when(repo.findByUserRoleId(userRoleId))
        .thenReturn(Optional.of(Staff.builder().id(UUID.randomUUID()).build()));

    assertThrows(
        DuplicateStaffException.class,
        () -> handler.handle(new CreateStaffCommand(userRoleId, entityId)));

    verify(repo).findByUserRoleId(userRoleId);
    verify(repo, never()).persist(any(Staff.class));
    verify(repo, never()).flush();
    verifyNoInteractions(em);
  }

  @Test
  void nullUserRoleId_violatesCommand_andSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new CreateStaffCommand(null, UUID.randomUUID())));
    verifyNoInteractions(repo, em);
  }

  @Test
  void nullEntityId_violatesCommand_andSkipsRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new CreateStaffCommand(UUID.randomUUID(), null)));
    verifyNoInteractions(repo, em);
  }
}
