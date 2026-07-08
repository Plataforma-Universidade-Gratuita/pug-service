package br.org.catolicasc.pug.shared.infra.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Abstract base class for JPA entities requiring a UUID primary key.
 *
 * <p>This mapped superclass provides a standard, immutable {@code id} field. The platform
 * specifically standardizes on UUID version 7 (UUIDv7) because its time-ordered nature heavily
 * optimizes database insert performance and prevents B-tree index fragmentation compared to fully
 * random UUIDv4s.
 */
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
