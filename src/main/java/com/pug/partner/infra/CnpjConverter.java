package com.pug.partner.infra;

import com.pug.partner.domain.Cnpj;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts between domain Cnpj and DB char(14). Use only if you choose to map CNPJ as a Cnpj type
 * in a JPA entity.
 */
@Converter(autoApply = false)
public final class CnpjConverter implements AttributeConverter<Cnpj, String> {
  @Override
  public String convertToDatabaseColumn(Cnpj attribute) {
    return attribute == null ? null : attribute.getValue();
  }

  @Override
  public Cnpj convertToEntityAttribute(String dbData) {
    return dbData == null ? null : Cnpj.of(dbData);
  }
}
