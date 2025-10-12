package com.pug.shared.domain.validation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.validation.Constraint;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Test;

class EmailBasicAnnotationTest {

  @Test
  void hasConstraintRetentionTargetAndDefaults() throws Exception {
    var c = EmailBasic.class.getAnnotation(Constraint.class);
    assertNotNull(c);
    assertArrayEquals(new Class[] {EmailBasicValidator.class}, c.validatedBy());

    assertNotNull(EmailBasic.class.getAnnotation(Retention.class));
    assertNotNull(EmailBasic.class.getAnnotation(Target.class));

    assertNotNull(EmailBasic.class.getMethod("message").getDefaultValue());
    assertNotNull(EmailBasic.class.getMethod("groups").getDefaultValue());
    assertNotNull(EmailBasic.class.getMethod("payload").getDefaultValue());
  }
}
