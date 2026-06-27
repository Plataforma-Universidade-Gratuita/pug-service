package br.org.catolicasc.pug.partner.domain;

import br.org.catolicasc.pug.partner.domain.enums.PartnerFieldErrorCodes;
import br.org.catolicasc.pug.partner.domain.vos.Cnpj;
import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.vos.AuditInfo;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import com.github.f4b6a3.uuid.UuidCreator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Domain Entity representing a Partner Organization (Entity) within the system.
 *
 * <p>This class acts as an aggregate root containing the partner's unique identifier, corporate
 * identification (CNPJ), location data, and audit tracking information. It extends {@link
 * DomainError} to accumulate validation failures across its own fields and to bubble up errors from
 * its nested value objects.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Entity extends DomainError {

  UUID id;

  Cnpj cnpj;

  String name;

  UUID cityId;

  String address;

  AuditInfo auditInfo;

  @Builder(toBuilder = true)
  private Entity(
      UUID id, Cnpj cnpj, String name, UUID cityId, String address, AuditInfo auditInfo) {
    this.id = id;
    this.cnpj = cnpj;
    this.name = name;
    this.cityId = cityId;
    this.address = address;
    this.auditInfo = auditInfo;
  }

  /**
   * Factory method to create a new {@code Entity} aggregate.
   *
   * <p>Automatically generates a time-ordered epoch UUID (UUIDv7) for the identifier, initializes
   * standard audit tracking information, and performs a full validation of the aggregate and its
   * contents.
   *
   * @param cnpj the {@link Cnpj} value object representing the corporate taxpayer ID
   * @param name the name of the partner entity
   * @param cityId the unique identifier of the city where the entity is located
   * @param address the physical street address
   * @return a newly created and self-validated {@link Entity} instance
   */
  public static Entity factory(Cnpj cnpj, String name, UUID cityId, String address) {
    Entity entity =
        Entity.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .cnpj(cnpj)
            .name(StringUtils.trim(name))
            .cityId(cityId)
            .address(StringUtils.trim(address))
            .auditInfo(AuditInfo.factory())
            .build();

    entity.collectValidationProblems();
    return entity;
  }

  /**
   * Updates the partner entity's name.
   *
   * <p>Since this entity is immutable, this method returns a new {@code Entity} instance with the
   * updated, trimmed name and a refreshed {@link AuditInfo} timestamp. The new instance is fully
   * re-validated.
   *
   * @param newName the new name of the partner entity
   * @return a new, updated, and validated {@link Entity} instance, or {@code this} if the name is
   *     unchanged
   */
  public Entity rename(String newName) {
    String trimmed = StringUtils.trim(newName);
    if (name.equals(trimmed)) {
      return this;
    }
    Entity updated = toBuilder().name(trimmed).auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Updates the physical address of the partner entity.
   *
   * @param newAddress the new address of the partner entity
   * @return a new, updated, and validated {@link Entity} instance, or {@code this} if the address
   *     is unchanged
   */
  public Entity moveToAddress(String newAddress) {
    String trimmed = StringUtils.trim(newAddress);
    if (address != null && address.equals(trimmed)) {
      return this;
    }
    Entity updated = toBuilder().address(trimmed).auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Updates the geographical location of the partner entity to a new city.
   *
   * @param newCityId the unique identifier of the new city
   * @return a new, updated, and validated {@link Entity} instance, or {@code this} if the city ID
   *     is unchanged
   */
  public Entity moveToCity(UUID newCityId) {
    if (cityId.equals(newCityId)) {
      return this;
    }
    Entity updated = toBuilder().cityId(newCityId).auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  private void collectValidationProblems() {
    validateIdField(id);
    if (cnpj == null) {
      addFieldError(PartnerFieldErrorCodes.INVALID_CNPJ_BLANK);
    } else if (cnpj.hasFieldErrors()) {
      addFieldErrors(cnpj.getFieldErrors());
    }
    validateNameField(name);
    if (cityId == null) {
      addFieldError(PartnerFieldErrorCodes.INVALID_CITY_ID_BLANK);
    }
    if (StringUtils.isNotEmpty(address) && address.length() > 254) {
      addFieldError(PartnerFieldErrorCodes.INVALID_ADDRESS_TOO_LONG);
    }
    if (auditInfo == null) {
      addFieldError(SharedFieldErrorCodes.INVALID_AUDIT_INFO_BLANK);
    } else if (auditInfo.hasFieldErrors()) {
      addFieldErrors(auditInfo.getFieldErrors());
    }
  }
}
