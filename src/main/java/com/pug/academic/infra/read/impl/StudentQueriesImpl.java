package com.pug.academic.infra.read.impl;

import com.pug.academic.infra.read.StudentQueries;
import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.shared.infra.search.HibernateSearchUtils;
import com.pug.shared.utils.CollectionUtils;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link StudentQueries} interface.
 *
 * <p>Executa consultas de leitura para perfis de estudantes, utilizando JPQL com expressões de
 * construtor para projetar os dados diretamente em {@link StudentView}.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class StudentQueriesImpl implements StudentQueries {

  @Inject EntityManager em;

  private static final String SELECT_BASE =
      """
                  select new com.pug.academic.infra.read.dtos.StudentView(
                    s.accountId,
                    s.academicRegistration,
                    s.campus,
                    s.courseId,
                    s.requiredHours,
                    s.completedHours,
                    s.concluded,
                    s.startDate,
                    s.dueDate,
                    s.createdAt,
                    s.updatedAt
                  )
                  from StudentEntity s
                  """;

  /** {@inheritDoc} */
  @Override
  public Optional<StudentView> findOptionalByAcademicRegistration(String academicRegistration) {
    if (StringUtils.isEmpty(academicRegistration)) {
      return Optional.empty();
    }
    return em.createQuery(SELECT_BASE + " where s.academicRegistration = :reg", StudentView.class)
        .setParameter("reg", academicRegistration)
        .getResultStream()
        .findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public Optional<StudentView> findOptionalByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return Optional.empty();
    }
    return em.createQuery(
            SELECT_BASE
                + " join AccountEntity acc on acc.id = s.accountId"
                + " join UserEntity u on u.id = acc.userId where u.cpf = :cpf",
            StudentView.class)
        .setParameter("cpf", cpf)
        .getResultStream()
        .findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public Optional<StudentView> findOptionalByEmail(String email) {
    if (StringUtils.isEmpty(email)) {
      return Optional.empty();
    }
    return em.createQuery(
            SELECT_BASE
                + " join AccountEntity acc on acc.id = s.accountId where acc.email = :email",
            StudentView.class)
        .setParameter("email", email)
        .getResultStream()
        .findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public Optional<StudentView> findOptionalById(UUID accountId) {
    if (accountId == null) {
      return Optional.empty();
    }
    return em.createQuery(SELECT_BASE + " where s.accountId = :id", StudentView.class)
        .setParameter("id", accountId)
        .getResultStream()
        .findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public List<StudentView> listAllByCourseId(UUID courseId) {
    if (courseId == null) {
      return List.of();
    }
    return em.createQuery(
            SELECT_BASE + " where s.courseId = :cid order by s.academicRegistration asc",
            StudentView.class)
        .setParameter("cid", courseId)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<StudentView> listAllStudents() {
    return em.createQuery(SELECT_BASE + " order by s.academicRegistration asc", StudentView.class)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<StudentView> listViewsByAccountIds(List<UUID> accountIds) {
    if (CollectionUtils.isEmpty(accountIds)) {
      return List.of();
    }
    return em.createQuery(
            SELECT_BASE + " where s.accountId in :ids order by s.academicRegistration asc",
            StudentView.class)
        .setParameter("ids", accountIds)
        .getResultList();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Utiliza Hibernate Search para localizar os usuários pelo nome e, em seguida, busca os
   * estudantes correspondentes.
   */
  @Override
  public List<StudentView> searchByName(String key) {
    List<UserEntity> userHits = HibernateSearchUtils.searchByName(em, UserEntity.class, key);
    if (userHits.isEmpty()) {
      return List.of();
    }

    List<UUID> userIds = CollectionUtils.toStream(userHits).map(UserEntity::getId).toList();

    return em.createQuery(
            SELECT_BASE
                + " join AccountEntity acc on acc.id = s.accountId where acc.userId in :ids",
            StudentView.class)
        .setParameter("ids", userIds)
        .getResultList();
  }
}
