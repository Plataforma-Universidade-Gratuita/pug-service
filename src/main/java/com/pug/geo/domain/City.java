package com.pug.geo.domain;

import com.pug.shared.id.UuidV7Hibernate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(
    name = "cities",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_cities_name", columnNames = "name"),
      @UniqueConstraint(name = "uk_cities_ibge_code", columnNames = "ibge_code")
    })
public class City {

  @Id
  @GeneratedValue
  @UuidGenerator(algorithm = UuidV7Hibernate.class)
  @Column(columnDefinition = "uuid", nullable = false, updatable = false)
  private UUID id;

  @NotBlank
  @Size(max = 100)
  @Column(length = 100, nullable = false, unique = true)
  private String name;

  @NotBlank
  @Size(min = 7, max = 7)
  @Pattern(regexp = "\\d{7}")
  @Column(name = "ibge_code", length = 7, nullable = false, unique = true)
  private String ibgeCode;
}
