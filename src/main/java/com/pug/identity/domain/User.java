package com.pug.identity.domain;

import com.pug.identity.infra.CpfConverter;
import com.pug.shared.id.UuidV7Hibernate;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(
    name = "users",
    uniqueConstraints = {@UniqueConstraint(name = "uk_users_cpf", columnNames = "cpf")})
public class User {

  @Id
  @GeneratedValue
  @UuidGenerator(algorithm = UuidV7Hibernate.class)
  @Column(columnDefinition = "uuid", nullable = false, updatable = false)
  private UUID id;

  @CPF
  @NotBlank
  @Convert(converter = CpfConverter.class)
  @Column(length = 11, nullable = false, unique = true)
  private String cpf;

  @NotBlank
  @Size(max = 150)
  @Column(length = 150, nullable = false)
  private String name;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;
}
