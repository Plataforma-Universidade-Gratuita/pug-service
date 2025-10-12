package com.pug.identity.service;

import com.pug.identity.domain.Cpf;
import com.pug.identity.domain.User;
import com.pug.identity.domain.UserRepository;
import com.pug.identity.service.commands.CreateUserCommand;
import com.pug.identity.service.commands.UpdateUserCommand;
import com.pug.shared.app.queries.StringQuery;
import com.pug.shared.app.queries.UuidQuery;
import com.pug.shared.domain.exceptions.AppValidationException;
import com.pug.shared.domain.exceptions.ResourceNotFoundException;
import com.pug.shared.errors.ErrorCodes;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.Optional;

@ApplicationScoped
public class UserService {

  @Inject UserRepository repo;

  @Transactional
  public User create(@Valid CreateUserCommand cmd) {
    var cpf = Cpf.of(cmd.cpf());
    if (repo.existsByCpf(cpf.getValue()))
      throw new AppValidationException(ErrorCodes.USER_CPF_ALREADY_IN_USE);
    return repo.save(User.builder().cpf(cpf).name(cmd.name()).build());
  }

  @Transactional
  public User update(@Valid UpdateUserCommand cmd) {
    var existing =
        getById(new UuidQuery(cmd.id()))
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.USER_NOT_FOUND));
    var newCpf = cmd.cpf() != null ? Cpf.of(cmd.cpf()) : existing.getCpf();
    if (repo.existsByCpfForAnother(newCpf.getValue(), cmd.id()))
      throw new AppValidationException(ErrorCodes.USER_CPF_ALREADY_IN_USE);
    var updated =
        existing.toBuilder()
            .cpf(newCpf)
            .name(cmd.name() != null ? cmd.name() : existing.getName())
            .build();
    return repo.save(updated);
  }

  public Optional<User> getById(@Valid UuidQuery q) {
    return repo.findOptionalById(q.id());
  }

  public Optional<User> findByCpf(@Valid StringQuery q) {
    return repo.findByCpf(Cpf.digits(q.value()));
  }
}
