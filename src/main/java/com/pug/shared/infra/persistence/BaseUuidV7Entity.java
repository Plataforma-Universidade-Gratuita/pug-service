package com.pug.shared.infra.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Base entityId class with a UUIDv7 identifier. */
@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseUuidV7Entity {
  @Id
  @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
  private UUID id;
}
