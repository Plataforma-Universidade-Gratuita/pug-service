package com.pug.partner.infra.read;

import com.pug.partner.infra.read.dtos.StaffView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Queries related to Staff.
 */
public interface IStaffQueries {

  /**
   * Finds a StaffView by its account ID.
   *
   * @param id the account ID of the staff member.
   * @return an Optional containing the StaffView if found, otherwise empty.
   */
  Optional<StaffView> findOptionalById(UUID id);

  /**
   * Finds a StaffView by its email.
   *
   * @param email the email address of the staff member.
   * @return an Optional containing the StaffView if found, otherwise empty.
   */
  Optional<StaffView> findOptionalByEmail(String email);

  /**
   * Lists all StaffView records.
   *
   * @return a list of all StaffView records.
   */
  List<StaffView> listAllStaff();

  /**
   * Lists StaffView records by CPF.
   *
   * @param cpf the CPF of the staff member.
   * @return a list of StaffView records matching the given CPF.
   */
  List<StaffView> listByCpf(String cpf);

  /**
   * Lists all StaffView records by entity ID.
   *
   * @param entityId the entity ID.
   * @return a list of StaffView records associated with the given entity ID.
   */
  List<StaffView> listAllByEntityId(UUID entityId);

  /**
   * Searches for StaffView records by name.
   *
   * @param key the search key (typically a user's name).
   * @return a list of StaffView records matching the search key.
   */
  List<StaffView> searchByName(String key);
}