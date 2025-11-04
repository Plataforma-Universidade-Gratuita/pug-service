package com.pug.shared.infra.persistence;

import com.pug.shared.domain.id.UuidV7Hibernate;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

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
