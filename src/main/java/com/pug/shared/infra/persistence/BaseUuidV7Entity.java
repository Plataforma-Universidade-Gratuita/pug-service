package com.pug.shared.infra.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

/** Base entity class with a UUIDv7 identifier. */
@Getter
@Setter
@MappedSuperclass
public abstract class BaseUuidV7Entity {
  @Id
  @GeneratedValue
  @UuidGenerator(algorithm = UuidV7Hibernate.class)
  @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
  private UUID id;
}
