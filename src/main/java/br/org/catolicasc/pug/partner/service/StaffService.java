package br.org.catolicasc.pug.partner.service;

import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.partner.service.dtos.StaffCreateCommand;
import br.org.catolicasc.pug.partner.service.dtos.StaffUpdateCommand;
import java.util.UUID;

/** Application service interface for managing the state of {@link Staff} domain aggregates. */
public interface StaffService {

  boolean delete(UUID accountId);

  long deleteAllByEntityId(UUID entityId);

  Staff getByAccountId(UUID accountId);

  Staff save(StaffCreateCommand cmd);

  /**
   * Updates an existing staff member, including optional transfer to a different partner entity.
   */
  Staff update(UUID accountId, StaffUpdateCommand cmd);

  /** Updates the activation status of the linked staff account. */
  Staff updateStatus(UUID accountId, boolean active);
}
