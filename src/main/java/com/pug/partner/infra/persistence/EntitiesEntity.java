package com.pug.partner.infra.persistence;

import com.pug.shared.infra.persistence.BaseUuidV7Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Builder
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
public class EntitiesEntity extends BaseUuidV7Entity {

  @NotBlank
  @Size(min = 14, max = 14)
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(name = "cnpj", nullable = false, length = 14, unique = true)
  private String cnpj;

  @NotBlank
  @Size(max = 150)
  @Column(name = "name", nullable = false, length = 150)
  private String name;

  @NotNull
  @Column(name = "city_id", nullable = false)
  private UUID cityId;

  @Size(max = 254)
  @Column(name = "address", length = 254)
  private String address;

  @NotNull
  @Column(name = "active", nullable = false)
  private Boolean active = Boolean.TRUE;
}
