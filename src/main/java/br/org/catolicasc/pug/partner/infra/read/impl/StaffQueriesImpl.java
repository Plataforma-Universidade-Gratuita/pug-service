package br.org.catolicasc.pug.partner.infra.read.impl;

import br.org.catolicasc.pug.partner.infra.read.StaffQueries;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.shared.infra.persistence.JpaSearchUtils;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link StaffQueries} interface using JPA.
 *
 * <p>This application-scoped bean executes read-only operations for staff profiles. Given the
 * deeply nested structure of a staff member crossing multiple domains (Staff -> Account -> User,
 * Staff -> Entity -> City), these queries rely on explicitly declared JPQL {@code JOIN} paths
 * inside constructor expressions to build out the full {@link StaffView} DTO without triggering N+1
 * select performance issues.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class StaffQueriesImpl implements StaffQueries {

  @Inject EntityManager em;

  private static final String SELECT_BASE =
      """
                  select new br.org.catolicasc.pug.partner.infra.read.dtos.StaffView(
                    new br.org.catolicasc.pug.identity.infra.read.dtos.AccountView(
                      acc.id,
                      acc.userId,
                      acc.email,
                      acc.accountType,
                      acc.createdAt,
                      acc.updatedAt,
                      acc.active
                    ),
                    e.id,
                    c.id
                  )
                  from StaffEntity s
                    join AccountEntity acc on acc.id = s.accountId
                    join UserEntity u on u.id = acc.userId
                    join EntityEntity e on e.id = s.entityId
                    join CityEntity c on c.id = e.cityId
                  """;

  private static final String ORDER_BY_PERSON_NAME_ASC = " order by u.name asc";

  /** {@inheritDoc} */
  @Override
  public Optional<StaffView> findOptionalByEmail(String email) {
    if (StringUtils.isEmpty(email)) {
      return Optional.empty();
    }
    var q =
        em.createQuery(SELECT_BASE + " where acc.email = :email", StaffView.class)
            .setParameter("email", email);
    return q.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public Optional<StaffView> findOptionalById(UUID accountId) {
    if (accountId == null) {
      return Optional.empty();
    }
    var q =
        em.createQuery(SELECT_BASE + " where s.accountId = :id", StaffView.class)
            .setParameter("id", accountId);
    return q.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public List<StaffView> listAllByEntityId(UUID entityId) {
    if (entityId == null) {
      return List.of();
    }
    var q =
        em.createQuery(
                SELECT_BASE + " where e.id = :eid" + ORDER_BY_PERSON_NAME_ASC, StaffView.class)
            .setParameter("eid", entityId);
    return q.getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<StaffView> listAllStaff() {
    return em.createQuery(SELECT_BASE + ORDER_BY_PERSON_NAME_ASC, StaffView.class).getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<StaffView> listByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return List.of();
    }
    var q =
        em.createQuery(
                SELECT_BASE + " where u.cpf = :cpf" + ORDER_BY_PERSON_NAME_ASC, StaffView.class)
            .setParameter("cpf", cpf);
    return q.getResultList();
  }

  @Override
  public List<StaffView> searchByName(String key) {
    if (StringUtils.isEmpty(key)) {
      return List.of();
    }
    return em.createQuery(
            SELECT_BASE
                + " where "
                + JpaSearchUtils.folded("u.name")
                + " like :pattern"
                + ORDER_BY_PERSON_NAME_ASC,
            StaffView.class)
        .setParameter("pattern", JpaSearchUtils.containsPattern(key))
        .getResultList();
  }
}
