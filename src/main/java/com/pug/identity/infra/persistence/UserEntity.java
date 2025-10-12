package com.pug.identity.infra.persistence;

import com.pug.identity.infra.CpfConverter;
import com.pug.shared.domain.id.UuidV7Hibernate;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
import org.hibernate.validator.constraints.br.CPF;

@Entity
@Table(
    name = "users",
    indexes = {@Index(name = "idx_users_name", columnList = "name")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class UserEntity {

  @Id
  @GeneratedValue
  @UuidGenerator(algorithm = UuidV7Hibernate.class)
  @Column(columnDefinition = "uuid", nullable = false, updatable = false)
  private UUID id;

  @CPF(message = "{error.identity.user.cpf.invalid}")
  @NotBlank
  @Convert(converter = CpfConverter.class)
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(name = "cpf", length = 11, nullable = false, unique = true)
  private String cpf;

  @NotBlank
  @Size(max = 150)
  @Column(name = "name", length = 150, nullable = false)
  private String name;
}
