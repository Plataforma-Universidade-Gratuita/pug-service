/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.geo.infra.persistence;

import br.org.catolicasc.pug.geo.domain.City;
import br.org.catolicasc.pug.shared.infra.persistence.BaseUuidV7Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * JPA entity representing a geographic City within the persistence layer.
 *
 * <p>This class acts as the database-mapped counterpart to the {@link City} domain aggregate. It
 * inherits a time-ordered UUIDv7 primary key and enforces strict uniqueness on the IBGE code at the
 * database level.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(
    callSuper = true,
    of = {"name", "ibgeCode"})
@Entity
@Table(
    name = "cities",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_cities_ibge_code",
          columnNames = {"ibge_code"})
    })
public class CityEntity extends BaseUuidV7Entity {

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(name = "ibge_code", nullable = false, length = 7, unique = true)
  private String ibgeCode;
}
