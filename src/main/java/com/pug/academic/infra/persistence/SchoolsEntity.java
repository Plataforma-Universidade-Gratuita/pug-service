package com.pug.academic.infra.persistence;

import com.pug.shared.infra.persistence.BaseUuidV7Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, of = "name")
@Entity
@Table(
    name = "schools",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_schools_name",
          columnNames = {"name"})
    })
public class SchoolsEntity extends BaseUuidV7Entity {

  @NotBlank
  @Size(max = 100)
  @Column(name = "name", nullable = false, length = 100, unique = true)
  private String name;
}
