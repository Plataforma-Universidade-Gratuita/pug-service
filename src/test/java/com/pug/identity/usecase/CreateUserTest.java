package com.pug.identity.usecase;

import org.junit.jupiter.api.Test;

class CreateUserTest {
  @Test
  void execute_runs() {
    new CreateUser().execute();
  }
}
