package com.pug.identity.service;

/**
 * Interface for hashing and verifying passwords using bcrypt with an added pepper.
 */
public interface IPasswordService {
    /**
     * Hashes the raw password combined with a pepper using bcrypt.
     *
     * @param raw the raw password
     * @return the bcrypt hash of the password with pepper
     */
    String hash(String raw);

    /**
     * Verifies a raw password against a stored bcrypt hash, considering the pepper.
     *
     * @param storedHash the stored bcrypt hash
     * @param raw        the raw password to verify
     * @return true if the password matches the hash, false otherwise
     */
    boolean verify(String storedHash, String raw);
}