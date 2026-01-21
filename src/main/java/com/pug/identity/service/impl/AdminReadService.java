package com.pug.identity.service.impl; // Pacote alterado

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.infra.read.IAdminQueries;
import com.pug.identity.infra.read.dtos.AdminView;
import com.pug.identity.service.IAdminReadService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for reading admin data.
 */
@ApplicationScoped
public class AdminReadService implements IAdminReadService { // Implementa IAdminReadService

  @Inject
  IAdminQueries queries; // Injeta a interface AdminQueries

  @Override // Adicione @Override para todos os métodos da interface
  public AdminView getViewById(UUID accountId) {
    return queries
            .findOptionalById(accountId)
            .orElseThrow(
                    () -> new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND, Map.of("accountId", accountId)));
  }

  @Override
  public AdminView getViewByEmail(String email) {
    // Adicionado validação de StringUtils.isEmpty
    if (StringUtils.isEmpty(email)) {
      throw new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND, Map.of("email", email));
    }
    return queries
            .findOptionalByEmail(email)
            .orElseThrow(
                    () -> new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND, Map.of("email", email)));
  }

  @Override
  public List<AdminView> listViews() {
    return queries.listAllAdmins();
  }

  @Override
  public List<AdminView> listViewsByCpf(String cpf) {
    // Adicionado validação de StringUtils.isEmpty
    if (StringUtils.isEmpty(cpf)) {
      return List.of();
    }
    return queries.listByCpf(cpf);
  }

  @Override
  public List<AdminView> search(String query) {
    // Aplica StringUtils.fold para consistência na pesquisa
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}