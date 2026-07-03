/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.partner.infra.persistence;

import br.org.catolicasc.pug.shared.infra.persistence.BaseAuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
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
 * JPA entity representing a Partner Organization (Entity) within the persistence layer.
 *
 * <p>This class acts as the database-mapped counterpart to the {@link
 * br.org.catolicasc.pug.partner.domain.Entity} domain aggregate. It inherits a time-ordered UUIDv7
 * primary key and standard audit tracking fields from {@link BaseAuditedEntity}. It enforces strict
 * uniqueness on the CNPJ at the database level.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(
    callSuper = true,
    of = {"name", "cnpj"})
@Entity
@Table(
    name = "entities",
    indexes = {
      @Index(name = "idx_entities_name", columnList = "name"),
      @Index(name = "idx_entities_city", columnList = "city_id")
    })
@SuperBuilder
public class EntityEntity extends BaseAuditedEntity {

  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(name = "cnpj", nullable = false, length = 14, unique = true)
  private String cnpj;

  @Column(name = "name", nullable = false, length = 150)
  private String name;

  @Column(name = "city_id", nullable = false)
  private UUID cityId;

  @Column(name = "address", length = 254)
  private String address;
}
