package com.pug.partner.domain;

import com.pug.shared.infra.persistence.Page;
import com.pug.shared.infra.persistence.PageRequest;
import java.util.Optional;
import java.util.UUID;

public interface PartnerEntityRepository {
  Optional<PartnerEntity> findOptionalById(UUID id);

  Optional<PartnerEntity> findByCnpj(String cnpjDigits);

  boolean existsByCnpjForAnother(String cnpjDigits, UUID excludeId);

  PartnerEntity save(PartnerEntity entity);

  Page<PartnerEntity> listByCity(UUID cityId, PageRequest pr);

  Page<PartnerEntity> searchByName(String pattern, PageRequest pr);
}
