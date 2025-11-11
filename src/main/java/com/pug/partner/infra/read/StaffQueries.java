package com.pug.partner.infra.read;

import com.pug.partner.infra.read.dtos.StaffView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Queries related to Staff. */
public interface StaffQueries {

  /**
   * Finds a StaffView by user ID.
   *
   * @param userId the user ID
   * @return an Optional containing the StaffView if found, otherwise empty
   */
  Optional<StaffView> findOptionalByUserId(UUID userId);

  /**
   * Finds a StaffView by email.
   *
   * @param email the email
   * @return an Optional containing the StaffView if found, otherwise empty
   */
  Optional<StaffView> findOptionalByEmail(String email);

  /**
   * Lists StaffView records by CPF.
   *
   * @param cpf the CPF
   * @return a list of StaffView records matching the given CPF
   */
  List<StaffView> listByCpf(String cpf);

  /**
   * Lists all StaffView records.
   *
   * @return a list of all StaffView records
   */
  List<StaffView> listAllStaff();

  /**
   * Lists all StaffView records by entity ID.
   *
   * @param entityId the entity ID
   * @return a list of StaffView records associated with the given entity ID
   */
  List<StaffView> listAllByEntityId(UUID entityId);

  /**
   * Searches for StaffView records by name.
   *
   * @param key the search key
   * @return a list of StaffView records matching the search key
   */
  List<StaffView> searchByName(String key);
}
