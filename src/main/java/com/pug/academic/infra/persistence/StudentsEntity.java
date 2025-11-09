package com.pug.academic.infra.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
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
@EqualsAndHashCode(of = "userId")
@ToString(of = {"userId", "academicRegistration"})
@Entity
@Table(
    name = "students",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_students_registration",
          columnNames = {"academic_registration"})
    },
    indexes = {@Index(name = "idx_students_course", columnList = "course_id")})
public class StudentsEntity {

  @Id
  @Column(name = "user_id", nullable = false, updatable = false)
  private UUID userId;

  @NotBlank
  @Size(max = 15)
  @Column(name = "academic_registration", nullable = false, length = 15, unique = true)
  private String academicRegistration;

  @NotBlank
  @Size(max = 150)
  @Column(name = "campus", nullable = false, length = 150)
  private String campus;

  @NotNull
  @Column(name = "course_id", nullable = false)
  private UUID courseId;

  @NotNull
  @DecimalMin(value = "0.00")
  @Column(name = "required_hours", nullable = false, precision = 6, scale = 2)
  private BigDecimal requiredHours;

  @NotNull
  @DecimalMin(value = "0.00")
  @Column(name = "completed_hours", nullable = false, precision = 6, scale = 2)
  private BigDecimal completedHours;

  @NotNull
  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @NotNull
  @Column(name = "due_date", nullable = false)
  private LocalDate dueDate;
}
