package com.pug.partner.infra.persistence;

import com.pug.partner.domain.PartnerEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PartnerEntityRepository implements PanacheRepositoryBase<PartnerEntity, UUID> {
  public boolean existsByCnpj(String cnpjDigits) {
    return count("cnpj", cnpjDigits) > 0;
  }

  public boolean existsByCnpjForAnother(String cnpj, UUID id) {
    return count("cnpj = ?1 and id <> ?2", cnpj, id) > 0;
  }

  public Optional<PartnerEntity> findByCnpj(String cnpjDigits) {
    return find("cnpj", cnpjDigits).firstResultOptional();
  }
}
