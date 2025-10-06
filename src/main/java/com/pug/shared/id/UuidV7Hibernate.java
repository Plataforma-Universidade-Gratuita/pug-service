package com.pug.shared.id;

import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.uuid.UuidValueGenerator;

public class UuidV7Hibernate implements UuidValueGenerator {
  @Override
  public UUID generateUuid(SharedSessionContractImplementor session) {
    return UuidCreator.getTimeOrderedEpoch();
  }
}
