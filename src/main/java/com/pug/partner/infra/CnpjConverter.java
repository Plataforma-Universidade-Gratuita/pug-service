package com.pug.partner.infra;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CnpjConverter implements AttributeConverter<String, String> {

  @Override
  public String convertToDatabaseColumn(String attribute) {
    if (attribute == null) return null;
    return attribute.replaceAll("\\D", "");
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    return dbData;
  }
}
