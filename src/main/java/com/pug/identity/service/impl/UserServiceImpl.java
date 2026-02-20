package com.pug.identity.service.impl;

import com.pug.identity.domain.User;
import com.pug.identity.domain.UserRepository;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.service.UserService;
import com.pug.identity.service.dtos.UserCreateCommand;
import com.pug.identity.service.dtos.UserUpdateCommand;
import com.pug.identity.service.utils.UserProcessor;
import com.pug.shared.domain.Problem;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DataIntegrityException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing users.
 */
@ApplicationScoped
public class UserServiceImpl implements UserService {

  private static final Logger LOG = Logger.getLogger(UserServiceImpl.class);

  @Inject
  UserRepository repo;

  @Transactional
  @Override
  public User save(UserCreateCommand cmd) {
    User userToPersist = UserProcessor.processCreateInput(cmd.cpfString(), cmd.name());

    if (userToPersist.hasErrors()) {
      throw new AppValidationException(userToPersist.getProblems());
    }

    if (existsByCpf(userToPersist.getCpf())) {
      LOG.errorf("User with CPF %s already exists", userToPersist.getCpf());
      throw new DuplicateResourceException(new Problem(IdentityErrorCodes.USER_ALREADY_EXISTS));
    }

    return repo.persist(userToPersist);
  }

  @Transactional
  @Override
  public User update(UUID id, UserUpdateCommand cmd) {
    User current = getById(id);
    User updated = UserProcessor.processUpdateInput(current, cmd.cpfString(), cmd.name());

    if (updated.hasErrors()) {
      throw new AppValidationException(updated.getProblems());
    }

    if (!updated.getCpf().equals(current.getCpf()) && existsByCpf(updated.getCpf())) {
      LOG.errorf("User with CPF %s already exists", updated.getCpf());
      throw new DuplicateResourceException(new Problem(IdentityErrorCodes.USER_ALREADY_EXISTS));
    }

    repo.update(updated);
    return getById(id);
  }

  @Transactional
  @Override
  public boolean delete(UUID id) {
    return repo.deleteById(id);
  }

  @Override
  public List<User> listAll() {
    List<User> users = repo.listAllUsers();

    for (User user : users) {
      if (user.hasErrors()) {
        LOG.errorf("Data integrity error: User with ID %s in DB violates domain rules. Problems: %s",
                user.getId(), user.getProblemsSummary());
        throw new DataIntegrityException();
      }
    }
    return users;
  }

  @Override
  public User getById(UUID id) {
    User user = repo.findOptionalById(id).orElseThrow(() -> {
      LOG.errorf("User with ID %s not found", id);
      return new ResourceNotFoundException(new Problem(IdentityErrorCodes.USER_NOT_FOUND));
    });

    if (user.hasErrors()) {
      LOG.errorf("Data integrity error: User with ID %s in DB violates domain rules. Problems: %s",
              id, user.getProblemsSummary());
      throw new DataIntegrityException();
    }

    return user;
  }

  @Override
  public boolean existsByCpf(Cpf cpf) {
    if (cpf == null) {
      return false;
    }
    return repo.existsByCpf(cpf.toString());
  }
}
