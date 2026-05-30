package br.org.catolicasc.pug.identity.infra.read.impl;

import br.org.catolicasc.pug.identity.infra.read.UsersQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.UserView;
import br.org.catolicasc.pug.identity.service.dtos.users.UserComplexSearchCriteria;
import br.org.catolicasc.pug.shared.infra.persistence.JpaSearchUtils;
import br.org.catolicasc.pug.shared.service.dtos.PageExecution;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link UsersQueries} interface using JPA.
 *
 * <p>This application-scoped bean handles the execution of read-only queries. It uses JPQL
 * constructor expressions to directly project database rows into lightweight {@link UserView} DTOs,
 * completely bypassing the overhead of instantiating managed JPA entities.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class UsersQueriesImpl implements UsersQueries {

  @Inject EntityManager em;

  /** {@inheritDoc} */
  @Override
  public Optional<UserView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var q =
        em.createQuery(
            "select new br.org.catolicasc.pug.identity.infra.read.dtos.UserView("
                + "p.id, p.cpf, p.name, p.createdAt, p.updatedAt) "
                + "from UserEntity p where p.id = :id",
            UserView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public List<UserView> listAllByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }
    return em.createQuery(
            "select new br.org.catolicasc.pug.identity.infra.read.dtos.UserView("
                + "u.id, u.cpf, u.name, u.createdAt, u.updatedAt) "
                + "from UserEntity u where u.id in :ids order by u.name asc",
            UserView.class)
        .setParameter("ids", ids)
        .getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<UserView> listAllUsers() {
    var q =
        em.createQuery(
            "select new br.org.catolicasc.pug.identity.infra.read.dtos.UserView("
                + "p.id, p.cpf, p.name, p.createdAt, p.updatedAt) "
                + "from UserEntity p order by p.name asc",
            UserView.class);
    return q.getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<UserView> search(PageQuery pageQuery, UserComplexSearchCriteria criteria) {
    List<String> clauses = new ArrayList<>();
    String cpf = criteria == null ? null : criteria.cpf();
    OffsetDateTime dateFrom = criteria == null ? null : criteria.dateFrom();
    OffsetDateTime dateTo = criteria == null ? null : criteria.dateTo();
    String name = criteria == null ? null : criteria.name();

    if (StringUtils.isNotEmpty(cpf)) {
      clauses.add(JpaSearchUtils.containsClause("u.cpf", "cpfPattern"));
    }
    if (dateFrom != null) {
      clauses.add("(u.createdAt >= :dateFrom or u.updatedAt >= :dateFrom)");
    }
    if (dateTo != null) {
      clauses.add("(u.createdAt <= :dateTo or u.updatedAt <= :dateTo)");
    }
    if (StringUtils.isNotEmpty(name)) {
      clauses.add(JpaSearchUtils.containsClause("u.name", "namePattern"));
    }

    String whereClause = clauses.isEmpty() ? "" : " where " + String.join(" and ", clauses);
    var countQuery =
        em.createQuery("select count(u.id) from UserEntity u" + whereClause, Long.class);
    bindSearchParameters(countQuery, cpf, dateFrom, dateTo, name);
    long totalElements = countQuery.getSingleResult();
    PageExecution pageExecution = PageExecution.from(pageQuery, totalElements);

    var dataQuery =
        em.createQuery(
            "select new br.org.catolicasc.pug.identity.infra.read.dtos.UserView("
                + "u.id, u.cpf, u.name, u.createdAt, u.updatedAt) "
                + "from UserEntity u"
                + whereClause
                + " order by u.name asc",
            UserView.class);
    bindSearchParameters(dataQuery, cpf, dateFrom, dateTo, name);

    return new PageResult<>(
        pageExecution.apply(dataQuery).getResultList(),
        pageExecution.page(),
        pageExecution.size(),
        pageExecution.totalElements(),
        pageExecution.totalPages());
  }

  private <T> void bindSearchParameters(
      jakarta.persistence.TypedQuery<T> query,
      String cpf,
      OffsetDateTime dateFrom,
      OffsetDateTime dateTo,
      String name) {
    if (StringUtils.isNotEmpty(cpf)) {
      JpaSearchUtils.bindContains(query, "cpfPattern", cpf);
    }
    if (dateFrom != null) {
      query.setParameter("dateFrom", dateFrom);
    }
    if (dateTo != null) {
      query.setParameter("dateTo", dateTo);
    }
    if (StringUtils.isNotEmpty(name)) {
      JpaSearchUtils.bindContains(query, "namePattern", name);
    }
  }
}
