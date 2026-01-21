package com.pug.academic.infra.persistence;

import com.pug.academic.domain.enums.Campi;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * StudentEntity represents the student data stored in the database.
 */
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "accountId")
@ToString(of = {"accountId", "academicRegistration"})
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
  @Column(name = "account_id", nullable = false, updatable = false)
  private UUID accountId;

  @Column(name = "academic_registration", nullable = false, length = 15)
  private String academicRegistration;

  @Enumerated(EnumType.STRING)
  @Column(name = "campus", nullable = false, length = 16)
  private Campi campus;

  @Column(name = "course_id", nullable = false)
  private UUID courseId;

  @Column(name = "required_hours", nullable = false, precision = 6, scale = 2)
  private BigDecimal requiredHours;

  @Column(name = "completed_hours", nullable = false, precision = 6, scale = 2)
  private BigDecimal completedHours;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "due_date", nullable = false)
  private LocalDate dueDate;
}