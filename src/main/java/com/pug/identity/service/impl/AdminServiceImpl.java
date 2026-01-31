package com.pug.identity.service.impl;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.Admin;
import com.pug.identity.domain.AdminRepository;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.service.AccountService;
import com.pug.identity.service.AdminService;
import com.pug.identity.service.dtos.AdminCreateCommand;
import com.pug.identity.service.dtos.AdminUpdateCommand;
import com.pug.identity.service.utils.AdminProcessor;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.time.TimeProvider;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;

/** Service for managing admins. */
@ApplicationScoped
public class AdminServiceImpl implements AdminService {

  private static final Logger LOG = Logger.getLogger(AdminServiceImpl.class);

  @Inject AdminRepository adminsRepo;
  @Inject AccountService accountService;
  @Inject TimeProvider time;

  @Transactional
  @Override
  public Admin save(AdminCreateCommand cmd) {
    Account account = accountService.save(cmd.accountCommand());

    Admin admin = AdminProcessor.processCreateInput(account.getId(), time);

    if (admin.hasErrors()) {
      throw new AppValidationException(admin.getProblems());
    }

    return adminsRepo.persist(admin);
  }

  @Transactional
  @Override
  public List<Admin> saveAll(Iterable<AdminCreateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    List<Account> accounts =
        accountService.saveAll(
            CollectionUtils.toStream(cmds)
                .map(AdminCreateCommand::accountCommand)
                .collect(Collectors.toList()));

    List<AppValidationException.Problem> problems = new ArrayList<>();
    List<Admin> adminsToPersist = new ArrayList<>();

    for (Account account : accounts) {
      Admin admin = AdminProcessor.processCreateInput(account.getId(), time);

      if (admin.hasErrors()) {
        problems.addAll(admin.getProblems());
      } else {
        adminsToPersist.add(admin);
      }
    }

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    return adminsRepo.persistAll(adminsToPersist);
  }

  @Transactional
  @Override
  public Admin update(UUID id, AdminUpdateCommand cmd) {
    accountService.update(id, cmd.accountCommand());
    return getById(id);
  }

  @Transactional
  @Override
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of(DeleteKeys.ADMINS, 0L, DeleteKeys.ACCOUNTS, 0L, DeleteKeys.USERS, 0L);
    }

    long adminsDeleted = adminsRepo.deleteByIds(ids);

    Map<DeleteKeys, Long> accountDeleteResult = accountService.deleteAll(ids);

    return Map.of(
        DeleteKeys.ADMINS, adminsDeleted,
        DeleteKeys.ACCOUNTS, accountDeleteResult.getOrDefault(DeleteKeys.ACCOUNTS, 0L),
        DeleteKeys.USERS, accountDeleteResult.getOrDefault(DeleteKeys.USERS, 0L));
  }

  @Override
  public Admin getById(UUID accountId) {
    Admin admin =
        adminsRepo
            .findOptionalById(accountId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        IdentityErrorCodes.ADMIN_NOT_FOUND, Map.of("accountId", accountId)));

    if (admin.hasErrors()) {
      LOG.errorf(
          "Data integrity error: "
              + "Admin with Account ID %s in DB violates domain rules. Problems: %s",
          accountId, admin.getProblemsSummary());
      throw new ResourceNotFoundException(
          IdentityErrorCodes.ADMIN_NOT_FOUND, Map.of("accountId", accountId));
    }

    return admin;
  }

  @Override
  public List<Admin> listAll() {
    List<Admin> admins = adminsRepo.listAllAdmins();

    for (Admin admin : admins) {
      if (admin.hasErrors()) {
        LOG.errorf(
            "Data integrity error: Corrupted Admin entity found in DB. Problems: %s",
            admin.getProblemsSummary());
        throw new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND);
      }
    }

    return admins;
  }

  @Override
  public boolean existsAnyByAccountIdIn(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return false;
    }
    return adminsRepo.existsAnyByAccountIdIn(ids);
  }
}
