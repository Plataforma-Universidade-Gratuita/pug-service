package com.pug.project.domain;

import com.pug.shared.id.UuidV7Hibernate;
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
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
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
    name = "projects_locations",
    indexes = {
      @Index(name = "idx_proj_loc_allocation", columnList = "project_allocation_id"),
      @Index(name = "idx_projects_locations_unaccent_address", columnList = "address")
    })
public class ProjectLocation {

  @Id
  @GeneratedValue
  @UuidGenerator(algorithm = UuidV7Hibernate.class)
  @Column(columnDefinition = "uuid", nullable = false, updatable = false)
  private UUID id;

  @NotNull
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(
      name = "project_allocation_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_proj_loc_allocation"))
  private ProjectAllocation projectAllocation;

  @Size(max = 254)
  @Column(length = 254)
  private String address;

  @Digits(integer = 3, fraction = 6)
  @DecimalMin(value = "-90")
  @DecimalMax(value = "90")
  @Column(precision = 9, scale = 6)
  private BigDecimal latitude;

  @Digits(integer = 3, fraction = 6)
  @DecimalMin(value = "-180")
  @DecimalMax(value = "180")
  @Column(precision = 9, scale = 6)
  private BigDecimal longitude;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;

  @SuppressFBWarnings(
      value = "UPM_UNCALLED_PRIVATE_METHOD",
      justification = "Invoked by Bean Validation via reflection")
  @AssertTrue(message = "{error.project_location.latlng.pair}")
  private boolean isLatLngPairValid() {
    return (latitude == null && longitude == null) || (latitude != null && longitude != null);
  }
}
