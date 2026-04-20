package br.org.catolicasc.pug.project.service;

import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.service.dtos.ProjectCreateCommand;
import br.org.catolicasc.pug.project.service.dtos.ProjectUpdateCommand;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Application service interface for managing the state of {@link Project} domain aggregates.
 *
 * <p>Following CQRS principles, this service handles the "Command" operations (Create, Update,
 * Delete) and strict domain-level retrievals. It manages the project lifecycle transitions and
 * enforces business invariants.
 */
public interface ProjectService {

  /**
   * Adiciona horas completadas ao progresso de um projeto.
   *
   * <p>Este método atualiza o progresso do projeto. Caso as horas completadas atinjam ou superem o
   * total oferecido, o projeto é automaticamente encerrado.
   *
   * @param id o identificador do projeto
   * @param hours a quantidade de horas a adicionar
   * @return o agregado {@link Project} atualizado
   * @throws ResourceNotFoundException se o projeto não existir
   */
  Project addCompletedHours(UUID id, BigDecimal hours);

  /**
   * Removes a {@link Project} from the system by its unique identifier.
   *
   * @param id the unique identifier (UUID) of the project to delete
   * @return {@code true} if deleted, {@code false} if not found
   */
  boolean delete(UUID id);

  /**
   * Checks if any project was created by a specific account.
   *
   * @param accountId the unique identifier of the account
   * @return {@code true} if a project exists
   */
  boolean existsByCreatedBy(UUID accountId);

  /**
   * Checks if any project exists for a specific entity.
   *
   * @param entityId the unique identifier of the entity
   * @return {@code true} if a project exists
   */
  boolean existsAnyByEntityId(UUID entityId);

  /**
   * Retrieves a full {@link Project} aggregate by its identifier.
   *
   * @param id the unique identifier (UUID) of the project
   * @return the {@link Project} aggregate
   * @throws ResourceNotFoundException if not found
   */
  Project getById(UUID id);

  /**
   * Instantiates and persists a new {@link Project} aggregate.
   *
   * @param cmd the command containing project data
   * @return the persisted {@link Project}
   */
  Project save(ProjectCreateCommand cmd);

  /**
   * Transitions a project to a new status.
   *
   * @param id the unique identifier (UUID) of the project
   * @param status the target {@link ProjectStatus}
   * @return the updated {@link Project}
   * @throws BusinessRuleException if the status transition is invalid
   */
  Project transitionStatus(UUID id, ProjectStatus status);

  /**
   * Updates an existing {@link Project}.
   *
   * @param id the unique identifier of the project
   * @param cmd the update command
   * @return the updated {@link Project}
   */
  Project update(UUID id, ProjectUpdateCommand cmd);
}
