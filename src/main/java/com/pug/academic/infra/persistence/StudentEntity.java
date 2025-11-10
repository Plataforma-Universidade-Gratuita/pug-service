package com.pug.academic.infra.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
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
public class StudentEntity {

  @Id
  @Column(name = "user_id", nullable = false, updatable = false)
  private UUID userId;

  @Size(max = 15)
  @Column(name = "academic_registration", nullable = false, length = 15, unique = true)
  private String academicRegistration;

  @Size(max = 150)
  @Column(name = "campus", nullable = false, length = 150)
  private String campus;

  @Column(name = "course_id", nullable = false)
  private UUID courseId;

  @DecimalMin(value = "0.00")
  @Column(name = "required_hours", nullable = false, precision = 6, scale = 2)
  private BigDecimal requiredHours;

  @DecimalMin(value = "0.00")
  @Column(name = "completed_hours", nullable = false, precision = 6, scale = 2)
  private BigDecimal completedHours;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "due_date", nullable = false)
  private LocalDate dueDate;
}
