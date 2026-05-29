package br.org.catolicasc.pug.identity.service;

/**
 * Utility service interface for cryptographic password operations.
 *
 * <p>This service abstracts the underlying hashing algorithms (e.g., bcrypt) and manages the
 * application of system-wide security enhancements (like peppering) to ensure credentials are
 * safely stored and verified.
 */
public interface PasswordService {

  /**
   * Generates a secure hash for a raw, plaintext password.
   *
   * <p>Prior to hashing, a system-configured pepper is appended to the raw password to protect
   * against pre-computed dictionary attacks (rainbow tables) in the event of a database compromise.
   *
   * @param raw the raw, plaintext password provided by the user
   * @return the securely hashed representation of the password (including the pepper)
   */
  String hash(String raw);

  /**
   * Evaluates whether a stored password hash represents a fully wired credential.
   *
   * <p>Accounts created through onboarding flows may intentionally start without any persisted
   * password hash. In those cases, authentication is temporarily passwordless until the user wires
   * their credentials for the first time.
   *
   * @param storedHash the persisted password hash retrieved from the data store
   * @return {@code true} when the account already has a configured password hash, {@code false}
   *     otherwise
   */
  boolean isConfigured(String storedHash);

  /**
   * Validates whether a raw password satisfies the platform's strength policy.
   *
   * <p>Implementations are expected to centralize all password-quality rules here so higher-level
   * services can reuse one consistent definition when wiring credentials or evolving password
   * policies in the future.
   *
   * @param raw the raw plaintext password proposed by the user
   * @throws br.org.catolicasc.pug.shared.exceptions.BusinessRuleException when the supplied
   *     password does not satisfy the platform's strength requirements
   */
  void validateStrength(String raw);

  /**
   * Evaluates whether a raw, plaintext password matches a previously stored hash.
   *
   * <p>This method applies the same system pepper to the raw input before performing the
   * cryptographic comparison against the stored hash. It intentionally returns a boolean so
   * higher-level authentication services can dictate the business flow (e.g., throwing unauthorized
   * exceptions or tracking failed attempts).
   *
   * @param storedHash the securely stored hash retrieved from the database
   * @param raw the raw, plaintext password provided by the user attempting to authenticate
   * @return {@code true} if the provided password (plus pepper) matches the stored hash, {@code
   *     false} otherwise
   */
  boolean verify(String storedHash, String raw);
}
