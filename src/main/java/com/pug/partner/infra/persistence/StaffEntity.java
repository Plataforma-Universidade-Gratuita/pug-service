package com.pug.partner.infra.persistence;

import com.pug.shared.domain.id.UuidV7Hibernate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(
    name = "staff",
    indexes = {
      @Index(name = "idx_staff_user", columnList = "user_id"),
      @Index(name = "idx_staff_entity", columnList = "entity_id")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class StaffEntity {

  @Id
  @GeneratedValue
  @UuidGenerator(algorithm = UuidV7Hibernate.class)
  @Column(columnDefinition = "uuid", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
  private UUID userId;

  @Column(name = "email", length = 254, nullable = false)
  private String email;

  @Column(name = "entity_id", columnDefinition = "uuid", nullable = false)
  private UUID entityId;

  @Column(name = "active", nullable = false)
  private boolean active;
}
