package com.pug.academic.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.pug.academic.domain.School;
import com.pug.academic.infra.persistence.SchoolEntity;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SchoolMapperTest {
  @Test
  void toDomainToEntityRoundTrip() {
    var id = UUID.randomUUID();
    var e = SchoolEntity.builder().id(id).name("UDESC").build();

    var d = SchoolMapper.toDomain(e);
    assertNotNull(d);
    assertEquals(id, d.getId());
    assertEquals("UDESC", d.getName());

    var back = SchoolMapper.toEntity(d);
    assertNotNull(back);
    assertEquals(id, back.getId());
    assertEquals("UDESC", back.getName());
  }

  @Test
  void copyWritesName() {
    var src = School.builder().id(UUID.randomUUID()).name("UFSC").build();
    var tgt = SchoolEntity.builder().id(src.getId()).name("Old").build();

    SchoolMapper.copy(src, tgt);
    assertEquals("UFSC", tgt.getName());
  }
}
