package com.pug.academic.infra.persistence;

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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(
    callSuper = true,
    of = {"name", "schoolId"})
@Entity
@Table(
    name = "courses",
    indexes = {@Index(name = "idx_courses_school", columnList = "school_id")})
public class CoursesEntity extends BaseUuidV7Entity {

  @NotBlank
  @Size(max = 120)
  @Column(name = "name", nullable = false, length = 120, unique = true)
  private String name;

  @NotNull
  @Column(name = "school_id", nullable = false)
  private UUID schoolId;
}
