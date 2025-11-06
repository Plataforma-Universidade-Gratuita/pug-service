package com.pug.shared.infra.persistence;

import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.uuid.UuidValueGenerator;

/** A Hibernate UUID generator that creates Version 7 UUIDs (time-ordered). */
public class UuidV7Hibernate implements UuidValueGenerator {
  /**
   * Generates a Version 7 UUID using the UuidCreator library.
   *
   * @param session the Hibernate session.
   * @return a time-ordered Version 7 UUID.
   */
  @Override
  public UUID generateUuid(SharedSessionContractImplementor session) {
    return UuidCreator.getTimeOrderedEpoch();
  }
}
