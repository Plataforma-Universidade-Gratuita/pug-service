package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.LoginRequest;

/**
 * Builder class for creating {@link LoginRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults for authentication payloads.
 */
public class LoginRequestBuilder {
  private String email = TestNameGenerator.generateUniqueEmail("pug.com");
  private String password = "password123";

  private LoginRequestBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link LoginRequestBuilder} instance
   */
  public static LoginRequestBuilder aLoginRequest() {
    return new LoginRequestBuilder();
  }

  /**
   * Sets the email address.
   *
   * @param email the email
   * @return this builder instance
   */
  public LoginRequestBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  /**
   * Sets the password.
   *
   * @param password the raw password
   * @return this builder instance
   */
  public LoginRequestBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  /**
   * Constructs the {@link LoginRequest} using the current builder state.
   *
   * @return a configured {@link LoginRequest} instance
   */
  public LoginRequest build() {
    return new LoginRequest(email, password);
  }
}
