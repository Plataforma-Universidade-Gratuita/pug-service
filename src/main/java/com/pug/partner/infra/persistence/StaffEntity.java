package com.pug.partner.infra.persistence;

import com.pug.identity.infra.persistence.UserEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Persistence entity representing a Staff member associated with an Entity. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "userId")
@ToString(of = {"userId", "entityId"})
@Entity
@Table(
    name = "staff",
    indexes = {@Index(name = "idx_staff_entity", columnList = "entity_id")})
@SuppressFBWarnings(
    value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"},
    justification =
        "JPA association; exposing managed UsersEntity is required for ORM and fetch joins.")
public class StaffEntity {

  @Id
  @Column(name = "user_id", nullable = false, updatable = false)
  private UUID userId;

  @NotNull
  @Column(name = "entity_id", nullable = false)
  private UUID entityId;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "entity_id", nullable = false, insertable = false, updatable = false)
  private EntitiesEntity entity;
}
