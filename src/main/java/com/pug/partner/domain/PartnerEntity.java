package com.pug.partner.domain;

import com.pug.geo.domain.City;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
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
    name = "entities",
    uniqueConstraints = @UniqueConstraint(name = "uk_entities_cnpj", columnNames = "cnpj"),
    indexes = @Index(name = "idx_entities_city", columnList = "city_id"))
public class PartnerEntity {

  @Id
  @GeneratedValue
  @UuidGenerator(algorithm = UuidV7Algorithm.class)
  @Column(columnDefinition = "uuid", nullable = false, updatable = false)
  private UUID id;

  @NotBlank
  @Size(max = 18)
  @Column(length = 18, nullable = false, unique = true)
  private String cnpj;

  @NotBlank
  @Size(max = 150)
  @Column(length = 150, nullable = false)
  private String name;

  @NotNull
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(
      name = "city_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_entities_city"))
  private City city;

  @Size(max = 254)
  @Column(length = 254)
  private String address;

  @Builder.Default
  @Column(nullable = false)
  private boolean active = true;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;
}
