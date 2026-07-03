/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.infra.persistence;

import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * JPA entity representing an enrolled FormerStudent within the persistence layer.
 *
 * <p>This class is the database-mapped counterpart to the {@link FormerStudent} domain aggregate.
 * Instead of a standalone ID, it uses the linked account's UUID as its primary key, effectively
 * functioning as a one-to-one extension of an authentication account.
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
    name = "former_students",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_former_students_registration",
          columnNames = {"academic_registration"})
    },
    indexes = {@Index(name = "idx_former_students_course", columnList = "course_id")})
public class FormerStudentEntity {

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

  @Column(name = "concluded", nullable = false)
  private Boolean concluded;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "due_date", nullable = false)
  private LocalDate dueDate;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;
}
