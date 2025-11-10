package com.pug.partner.infra.queries;

import com.pug.partner.infra.read.EntityQueries;
import com.pug.partner.infra.read.dtos.EntityView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class EntityQueriesImpl implements EntityQueries {

  @Inject
  EntityManager em;

  private static String sanitizeCnpj(String cnpj) {
    if (cnpj == null) return null;
    String digits = cnpj.replaceAll("\\D", "");
    return digits.length() == 14 ? digits : null;
  }

  @Override
  public Optional<EntityView> findOptionalById(UUID id) {
    if (id == null) return Optional.empty();
    var q =
            em.createQuery(
                    """
                            select new com.pug.partner.infra.read.dtos.EntityView(
                              e.id, e.cnpj, e.name, e.address,
                              new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode)
                            )
                            from EntityEntity e
                              join com.pug.geo.infra.persistence.CityEntity c on c.id = e.cityId
                            where e.id = :id
                            """,
                    EntityView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  @Override
  public Optional<EntityView> findByCnpj(String cnpj) {
    String digits = sanitizeCnpj(cnpj);
    if (digits == null) return Optional.empty();
    var q =
            em.createQuery(
                    """
                            select new com.pug.partner.infra.read.dtos.EntityView(
                              e.id, e.cnpj, e.name, e.address,
                              new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode)
                            )
                            from EntityEntity e
                              join com.pug.geo.infra.persistence.CityEntity c on c.id = e.cityId
                            where e.cnpj = :cnpj
                            """,
                    EntityView.class);
    q.setParameter("cnpj", digits);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<EntityView> listAllEntities() {
    return em.createQuery(
                    """
                            select new com.pug.partner.infra.read.dtos.EntityView(
                              e.id, e.cnpj, e.name, e.address,
                              new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode)
                            )
                            from EntityEntity e
                              join com.pug.geo.infra.persistence.CityEntity c on c.id = e.cityId
                            order by e.name
                            """,
                    EntityView.class)
            .getResultList();
  }

  @Override
  public List<EntityView> listAllByCityId(UUID cityId) {
    if (cityId == null) return List.of();
    var q =
            em.createQuery(
                    """
                            select new com.pug.partner.infra.read.dtos.EntityView(
                              e.id, e.cnpj, e.name, e.address,
                              new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode)
                            )
                            from EntityEntity e
                              join com.pug.geo.infra.persistence.CityEntity c on c.id = e.cityId
                            where e.cityId = :cityId
                            order by e.name
                            """,
                    EntityView.class);
    q.setParameter("cityId", cityId);
    return q.getResultList();
  }


}
