package com.pug.identity.infra.queries;

import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.UserQueries;
import com.pug.identity.infra.read.dtos.UserView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Implementation of PersonQueries using JPA. */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class UserQueriesImpl implements UserQueries {

    @Inject
    EntityManager em;

    private static UserView toView(UserEntity e) {
        if (e == null) {
            return null;
        }
        return new UserView(
            e.getId(),
            e.getCpf(),
            e.getName(),
            e.getCreatedAt()
        );
    }

    @Override
    public Optional<UserView> findOptionalById(UUID id){
        if (id == null) {
            return Optional.empty();
        }
        var q =
            em.createQuery(
                "select new com.pug.identity.infra.read.dtos.UserView("
                    + "p.id, p.cpf, p.name, p.createdAt) "
                    + "from UserEntity p where p.id = :id",
                UserView.class);
        q.setParameter("id", id);
        return q.getResultStream().findFirst();
    }

    @Override
    public Optional<UserView> findOptionalByCpf(String cpf){
        if (cpf == null || cpf.isEmpty()) {
            return Optional.empty();
        }
        var q =
            em.createQuery(
                "select new com.pug.identity.infra.read.dtos.UserView("
                    + "p.id, p.cpf, p.name, p.createdAt) "
                    + "from UserEntity p where p.cpf = :cpf",
                UserView.class);
        q.setParameter("cpf", cpf);
        return q.getResultStream().findFirst();
    }

    @Override
    public List<UserView> listAllPeople() {
        var q = em.createQuery(
                "select new com.pug.identity.infra.read.dtos.UserView(" +
                    "p.id, p.cpf, p.name, p.createdAt) " +
                    "from UserEntity p order by p.name asc",
                UserView.class
        );
        return q.getResultList();
    }

    @Override
    public List<UserView> searchByName(String key){
        if (key == null || key.isBlank()) {
            return List.of();
        }

        String[] tokens = key.split("\\s+");
        SearchSession s = Search.session(em);

        List<UserEntity> hits =
            s.search(UserEntity.class)
                .where(
                    f ->
                        f.bool(
                            b -> {
                                b.should(f.wildcard().field("name_exact").matching(key + "*").boost(8f));
                                b.should(
                                    f.wildcard().field("name_exact").matching("*" + key + "*").boost(6f));
                                for (String t : tokens) {
                                    if (t.length() >= 3) {
                                        b.should(
                                            f.wildcard()
                                                .field("name_exact")
                                                .matching("*" + t + "*")
                                                .boost(3f));
                                    }
                                }
                                b.should(f.match().field("name").matching(key).fuzzy(1).boost(4f));
                                b.should(f.match().field("name_auto").matching(key).boost(2f));
                            }))
                .sort(f -> f.score().then().field("name_sort"))
                .fetchAllHits();

        if (hits.isEmpty()) {
            return List.of();
        }

        List<UserView> out = new ArrayList<>(hits.size());
        for (UserEntity p : hits) {
            out.add(toView(p));
        }
        return out;
    }
}
