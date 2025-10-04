package com.pug.student.domain;

import com.pug.identity.domain.UserRoleAssignment;
import com.pug.shared.id.UuidV7Algorithm;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
import org.hibernate.annotations.UuidGenerator;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(
    name = "students",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_students_user_role", columnNames = "user_role_id"),
      @UniqueConstraint(
          name = "uk_students_academic_registration",
          columnNames = "academic_registration")
    },
    indexes = @Index(name = "idx_students_course", columnList = "course_id"))
public class Student {

  @Id
  @GeneratedValue
  @UuidGenerator(algorithm = UuidV7Algorithm.class)
  @Column(columnDefinition = "uuid", nullable = false, updatable = false)
  private UUID id;

  @NotNull
  @OneToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_role_id",
      nullable = false,
      unique = true,
      foreignKey = @ForeignKey(name = "fk_students_user_role"))
  private UserRoleAssignment userRole;

  @NotBlank
  @Size(max = 15)
  @Column(name = "academic_registration", length = 15, nullable = false, unique = true)
  private String academicRegistration;

  @NotNull
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(
      name = "course_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_students_course"))
  private Course course;
}
