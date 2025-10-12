package com.pug.geo.infra.persistence;

import com.pug.shared.domain.id.UuidV7Hibernate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    name = "cities",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_cities_name", columnNames = "name"),
      @UniqueConstraint(name = "uk_cities_ibge_code", columnNames = "ibge_code")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class CityEntity {

  @Id
  @GeneratedValue
  @UuidGenerator(algorithm = UuidV7Hibernate.class)
  @Column(columnDefinition = "uuid", nullable = false, updatable = false)
  private UUID id;

  @NotBlank
  @Size(max = 100)
  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @NotBlank
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(name = "ibge_code", length = 7, nullable = false)
  private String ibgeCode;
}
