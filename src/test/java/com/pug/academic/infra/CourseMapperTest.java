package com.pug.academic.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.pug.academic.domain.Course;
import com.pug.academic.infra.persistence.CourseEntity;
import com.pug.academic.infra.persistence.SchoolEntity;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CourseMapperTest {
  @Test
  void toDomainToEntityRoundTrip() {
    var sid = UUID.randomUUID();
    var school = SchoolEntity.builder().id(sid).name("UFSC").build();
    var e = CourseEntity.builder().id(UUID.randomUUID()).name("Medicine").school(school).build();

    var d = CourseMapper.toDomain(e);
    assertNotNull(d);
    assertEquals(e.getId(), d.getId());
    assertEquals("Medicine", d.getName());
    assertEquals(sid, d.getSchoolId());

    var back = CourseMapper.toEntity(d, school);
    assertNotNull(back);
    assertEquals(e.getId(), back.getId());
    assertEquals("Medicine", back.getName());
    assertEquals(sid, back.getSchool().getId());
  }

  @Test
  void copyWritesFields() {
    var sid = UUID.randomUUID();
    var school = SchoolEntity.builder().id(sid).name("UFSC").build();
    var src = Course.builder().id(UUID.randomUUID()).name("A").schoolId(sid).build();
    var tgt = CourseEntity.builder().id(src.getId()).name("B").school(school).build();

    CourseMapper.copy(src, tgt, school);
    assertEquals("A", tgt.getName());
    assertEquals(sid, tgt.getSchool().getId());
  }
}
