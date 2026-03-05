package com.pug.projects.infra.read.impl;

import com.pug.projects.infra.read.ProjectQueries;
import com.pug.projects.infra.read.dtos.ProjectView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link ProjectQueries} interface.
 * <p>
 * Uses JPQL constructor expressions to implicitly join the project with its
 * Partner Entity, City, and Creator Account in a single, highly optimized query.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class ProjectQueriesImpl implements ProjectQueries {

    @Inject
    EntityManager em;

    private static final String SELECT_BASE =
            """
                    select new com.pug.projects.infra.read.dtos.ProjectView(
                      p.id, p.name,
                      new com.pug.partner.infra.read.dtos.EntityView(
                        ent.id, ent.cnpj, ent.name, ent.address,
                        new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode),
                        ent.createdAt, ent.updatedAt
                      ),
                      p.description,
                      new com.pug.identity.infra.read.dtos.AccountView(
                        acc.id,
                        new com.pug.identity.infra.read.dtos.UserView(
                          u.id, u.cpf, u.name, u.createdAt, u.updatedAt
                        ),
                        acc.email, acc.accountType, acc.createdAt, acc.updatedAt
                      ),
                      p.maxParticipants, p.offeredHours, p.status,
                      p.closedAt, p.createdAt, p.updatedAt
                    )
                    from ProjectEntity p
                    join EntityEntity ent on ent.id = p.entityId
                    join CityEntity c on c.id = ent.cityId
                    join AccountEntity acc on acc.id = p.createdBy
                    join UserEntity u on u.id = acc.userId
                    """;

    private static final String ORDER_BY_NAME = " order by p.name asc";

    @Override
    public Optional<ProjectView> findOptionalById(UUID id) {
        if (id == null) return Optional.empty();
        var q = em.createQuery(SELECT_BASE + " where p.id = :id", ProjectView.class);
        q.setParameter("id", id);
        return q.getResultStream().findFirst();
    }

    @Override
    public List<ProjectView> listAllProjects() {
        return em.createQuery(SELECT_BASE + ORDER_BY_NAME, ProjectView.class).getResultList();
    }

    @Override
    public List<ProjectView> listByEntityId(UUID entityId) {
        if (entityId == null) return List.of();
        var q = em.createQuery(SELECT_BASE + " where p.entityId = :eid" + ORDER_BY_NAME, ProjectView.class);
        q.setParameter("eid", entityId);
        return q.getResultList();
    }

    @Override
    public List<ProjectView> searchByName(String query) {
        if (query == null || query.isBlank()) return List.of();
        var q = em.createQuery(SELECT_BASE + " where lower(p.name) like :name" + ORDER_BY_NAME, ProjectView.class);
        q.setParameter("name", "%" + query.toLowerCase() + "%");
        return q.getResultList();
    }
}