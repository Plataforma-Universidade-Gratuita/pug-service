package com.pug.partner.usecase.entity;

import com.pug.partner.usecase.entity.create.RegisterPartnerEntityHandler;
import org.junit.jupiter.api.Test;

class RegisterEntityTest {
  @Test
  void execute_runs() {
    new RegisterPartnerEntityHandler().execute();
  }
}
