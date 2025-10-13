package com.pug.partner.infra.persistence;

import com.pug.partner.domain.Cnpj;
import com.pug.partner.infra.CnpjConverter;
import com.pug.shared.domain.id.UuidV7Hibernate;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
    name = "entities",
    indexes = {
      @Index(name = "idx_entities_name", columnList = "name"),
      @Index(name = "idx_entities_city", columnList = "city_id")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class PartnerEntityEntity {

  @Id
  @GeneratedValue
  @UuidGenerator(algorithm = UuidV7Hibernate.class)
  @Column(columnDefinition = "uuid", nullable = false, updatable = false)
  private UUID id;

  @JdbcTypeCode(SqlTypes.CHAR)
  @Convert(converter = CnpjConverter.class)
  @Column(name = "cnpj", nullable = false, unique = true)
  private Cnpj cnpj;

  @Column(name = "name", length = 150, nullable = false)
  private String name;

  @Column(name = "city_id", columnDefinition = "uuid", nullable = false)
  private UUID cityId;

  @Column(name = "address", length = 254)
  private String address;

  @Column(name = "active", nullable = false)
  private boolean active;
}
