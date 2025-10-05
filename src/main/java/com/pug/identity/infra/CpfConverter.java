package com.pug.identity.infra;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CpfConverter implements AttributeConverter<String, String> {
  @Override
  public String convertToDatabaseColumn(String attr) {
    if (attr == null) return null;
    return attr.replaceAll("\\D", "");
  }

  @Override
  public String convertToEntityAttribute(String db) {
    return db;
  }
}
