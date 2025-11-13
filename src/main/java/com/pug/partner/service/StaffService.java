package com.pug.partner.service;

import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.identity.service.AccountService;
import com.pug.identity.service.PasswordService;
import com.pug.partner.domain.Staff;
import com.pug.partner.domain.StaffRepository;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.StreamSupport;

/** Service for managing staff assignments to partner entities. */
@ApplicationScoped
public class StaffService {

  @Inject StaffRepository repo;
  @Inject AccountService accountService;
  @Inject EntityService entityService;
  @Inject PasswordService passwords;

  /**
   * Save a new staff member by creating a user and linking them to an entity.
   *
   * @param cpf the CPF of the staff member.
   * @param name the name of the staff member.
   * @param email the email of the staff member.
   * @param rawPassword the raw password for the staff member.
   * @param entityId the ID of the entity to which the staff member will be linked.
   * @return the created Staff object.
   * @throws DuplicateResourceException if a staff member with the same user ID already exists.
   * @throws ResourceNotFoundException if the specified entity does not exist.
   */
  @Transactional
  public Staff save(Cpf cpf, String name, Email email, String rawPassword, UUID entityId) {
    Objects.requireNonNull(cpf);
    Objects.requireNonNull(name);
    Objects.requireNonNull(email);
    Objects.requireNonNull(rawPassword);
    Objects.requireNonNull(entityId);

    entityService.getById(entityId);
    var user =
        accountService.save(cpf, name, email, AccountType.PARTNER, passwords.hash(rawPassword));

    if (repo.existsByAccountId(user.getId())) {
      throw new DuplicateResourceException(PartnerErrorCodes.STAFF_ALREADY_EXISTS);
    }
    return repo.persist(Staff.createNew(user.getId(), entityId));
  }

  /**
   * Save multiple existing users as staff members to a specified entity.
   *
   * @param entityId the ID of the entity to which the staff members will be linked.
   * @param userIds an iterable of user IDs to be assigned as staff members.
   * @return a list of created Staff objects.
   * @throws DuplicateResourceException if any of the specified users are already staff members.
   * @throws ResourceNotFoundException if the specified entity or any user does not exist.
   */
  @Transactional
  public List<Staff> saveAll(UUID entityId, Iterable<UUID> userIds) {
    Objects.requireNonNull(entityId);
    Objects.requireNonNull(userIds);
    entityService.getById(entityId);

    Set<UUID> unique =
        StreamSupport.stream(userIds.spliterator(), false)
            .filter(Objects::nonNull)
            .collect(LinkedHashSet::new, Set::add, Set::addAll);
    if (unique.isEmpty()) {
      return List.of();
    }

    List<Staff> batch = new ArrayList<>(unique.size());
    for (UUID uid : unique) {
      if (repo.existsByAccountId(uid)) {
        throw new DuplicateResourceException(PartnerErrorCodes.STAFF_ALREADY_EXISTS);
      }
      accountService.getById(uid);
      batch.add(Staff.createNew(uid, entityId));
    }
    return repo.persistAll(batch);
  }

  /**
   * Deletes staff members by their user IDs and removes the associated user accounts.
   *
   * @param userIds an iterable of user IDs of the staff members to be deleted.
   * @return a map containing the counts of deleted staff members and user accounts.
   */
  @Transactional
  public Map<DeleteKeys, Long> deleteByUserIds(Iterable<UUID> userIds) {
    if (userIds == null) {
      return Map.of();
    }

    List<UUID> existing = new ArrayList<>();
    for (UUID id : userIds) {
      if (id != null && repo.existsByAccountId(id)) {
        existing.add(id);
      }
    }
    if (existing.isEmpty()) {
      return Map.of();
    }

    long staff = repo.deleteByIds(existing);
    long user = accountService.deleteAll(existing);
    return Map.of("staff", staff, "users", user);
  }

  /**
   * Retrieves a staff member by their user ID.
   *
   * @param userId the ID of the staff user.
   * @return the Staff object.
   * @throws ResourceNotFoundException if the specified staff member does not exist.
   */
  public Staff get(UUID userId) {
    Objects.requireNonNull(userId);
    return repo.findOptionalById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND));
  }

  /**
   * Lists all staff members.
   *
   * @return a list of all Staff objects.
   */
  public List<Staff> listAll() {
    return repo.listAllStaff();
  }

  /**
   * Lists all staff members associated with a specific entity.
   *
   * @param entityId the ID of the entity.
   * @return a list of Staff objects linked to the specified entity.
   */
  public List<Staff> listByEntity(UUID entityId) {
    Objects.requireNonNull(entityId);
    return repo.listAllByEntityId(entityId);
  }

  /**
   * Checks if a staff member exists by their user ID.
   *
   * @param userId the ID of the staff user.
   * @return true if the staff member exists, false otherwise.
   */
  public boolean existsByUserId(UUID userId) {
    Objects.requireNonNull(userId);
    return repo.existsByAccountId(userId);
  }

  public boolean existsAnyByAccountIdIn(Iterable<UUID> accountIds) {
    if (CollectionUtils.isEmpty(accountIds)) {
      return false;
    }
    return repo.existsAnyByAccountIdIn(accountIds);
  }
}
