package com.pug.identity.service.impl; // Pacote alterado

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.infra.read.IUserQueries;
import com.pug.identity.infra.read.dtos.UserView;
import com.pug.identity.service.IUserReadService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Read-only service for user views. */
@ApplicationScoped
public class UserReadService implements IUserReadService { // Implementa IUserReadService

  @Inject IUserQueries queries; // Injeta a interface IUserQueries

  @Override // Adicione @Override para todos os métodos da interface
  public UserView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND, Map.of("id", id)));
  }

  @Override
  public UserView getViewByCpf(String cpf) {
    // Adicionado validação de StringUtils.isEmpty
    if (StringUtils.isEmpty(cpf)) {
      throw new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND, Map.of("cpf", cpf));
    }
    return queries
        .findOptionalByCpf(cpf)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    IdentityErrorCodes.USER_NOT_FOUND, Map.of("cpf", cpf)));
  }

  @Override
  public List<UserView> listViews() {
    return queries.listAllUsers();
  }

  @Override
  public List<UserView> search(String query) {
    // Aplica StringUtils.fold para consistência na pesquisa
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}
