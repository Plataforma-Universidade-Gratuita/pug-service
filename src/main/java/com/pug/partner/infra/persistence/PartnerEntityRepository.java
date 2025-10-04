package com.pug.partner.infra.persistence;

import com.pug.partner.domain.PartnerEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PartnerEntityRepository implements PanacheRepository<PartnerEntity> {}
