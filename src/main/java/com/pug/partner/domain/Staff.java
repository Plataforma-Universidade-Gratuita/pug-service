package com.pug.partner.domain;

import com.pug.identity.domain.Role;
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
import jakarta.validation.constraints.NotNull;
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
    name = "staff",
    uniqueConstraints =
        @UniqueConstraint(name = "uk_staff_user_role", columnNames = "user_role_id"),
    indexes = @Index(name = "idx_staff_entity", columnList = "entity_id"))
public class Staff {

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
      foreignKey = @ForeignKey(name = "fk_staff_user_role"))
  private Role userRole;

  @NotNull
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(
      name = "entity_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_staff_entity"))
  private PartnerEntity entity;
}
