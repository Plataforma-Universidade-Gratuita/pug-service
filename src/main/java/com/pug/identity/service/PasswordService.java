package com.pug.identity.service;

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
