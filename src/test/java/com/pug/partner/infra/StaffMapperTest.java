package com.pug.partner.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.helpers.domainGenerators.EntityGenerator;
import com.pug.helpers.domainGenerators.StaffGenerator;
import com.pug.helpers.domainGenerators.UserGenerator;
import com.pug.helpers.entityGenerators.CitiesEntityGenerator;
import com.pug.helpers.entityGenerators.EntitiesEntityGenerator;
import com.pug.helpers.entityGenerators.StaffEntityGenerator;
import com.pug.helpers.entityGenerators.UsersEntityGenerator;
import com.pug.identity.infra.persistence.UsersEntity;
import com.pug.partner.domain.Entity;
import com.pug.partner.domain.Staff;
import com.pug.partner.infra.persistence.EntitiesEntity;
import com.pug.partner.infra.persistence.StaffEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaffMapperTest {

  private final StaffGenerator staffGen = new StaffGenerator();
  private final UserGenerator userGen = new UserGenerator();
  private final EntityGenerator entityGen = new EntityGenerator();

  private final UsersEntityGenerator usersEntityGen = new UsersEntityGenerator();
  private final StaffEntityGenerator staffEntityGen = new StaffEntityGenerator();
  private final EntitiesEntityGenerator entitiesEntityGen = new EntitiesEntityGenerator();
  private final CitiesEntityGenerator citiesEntityGen = new CitiesEntityGenerator();

  @Test
  @DisplayName("toEntity maps ids and creates references")
  void toEntity_basic() {
    Staff d = staffGen.createRandomPersistedStaff();

    StaffEntity e = StaffMapper.toEntity(d);
    assertNotNull(e);

    assertEquals(d.getUser().getId(), e.getUserId());
    assertEquals(d.getEntity().getId(), e.getEntityId());

    assertNotNull(e.getUser());
    assertEquals(d.getUser().getId(), e.getUser().getId());

    assertNotNull(e.getEntity());
    assertEquals(d.getEntity().getId(), e.getEntity().getId());
  }

  @Test
  @DisplayName("toDomain maps with full UsersEntity and EntitiesEntity (with valid CitiesEntity)")
  void toDomain_withRelations() {
    UsersEntity user = usersEntityGen.createRandomUsersEntity();
    user.setId(UuidCreator.getTimeOrderedEpoch());

    var city = citiesEntityGen.createRandomCitiesEntity();
    city.setId(UuidCreator.getTimeOrderedEpoch());

    EntitiesEntity entity = entitiesEntityGen.createRandomEntitiesEntity(city.getId());
    entity.setId(entityGen.createRandomPersistedEntity().getId());
    entity.setCity(city);
    entity.setCityId(city.getId());

    var se = new StaffEntity();
    se.setUser(user);
    se.setEntity(entity);
    se.setUserId(user.getId());
    se.setEntityId(entity.getId());

    Staff d = StaffMapper.toDomain(se);
    assertNotNull(d);
    assertEquals(user.getId(), d.getUser().getId());
    assertEquals(entity.getId(), d.getEntity().getId());
  }

  @Test
  @DisplayName("copy updates only entity reference and id")
  void copy_updatesEntityOnly() {
    var originalUserId = userGen.createRandomPersistedUser().getId();
    var originalEntity = entityGen.createRandomPersistedEntity();

    StaffEntity target =
        staffEntityGen.createRandomStaffEntity(originalUserId, originalEntity.getId());

    var validUser = userGen.createRandomPersistedUser().toBuilder().id(originalUserId).build();

    Entity newEntity = entityGen.createRandomPersistedEntity();

    Staff domain = Staff.builder().user(validUser).entity(newEntity).build();

    StaffMapper.copy(domain, target);

    assertEquals(originalUserId, target.getUserId(), "userId unchanged");
    assertEquals(newEntity.getId(), target.getEntityId(), "entityId updated");
    assertNotNull(target.getEntity());
    assertEquals(newEntity.getId(), target.getEntity().getId());
  }

  @Test
  @DisplayName("null inputs handled")
  void nulls() {
    assertNull(StaffMapper.toEntity(null));
    assertNull(StaffMapper.toDomain(null));

    StaffEntity e = new StaffEntity();
    StaffMapper.copy(null, e); // no throw
    StaffMapper.copy(staffGen.createRandomPersistedStaff(), null);
  }
}
