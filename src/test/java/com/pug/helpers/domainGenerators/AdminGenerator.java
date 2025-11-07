package com.pug.helpers.domainGenerators;

import com.pug.identity.domain.Admin;
import com.pug.identity.domain.User;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/** Helper to generate Admin aggregates for tests. */
public final class AdminGenerator {
  private final ThreadLocalRandom rnd = ThreadLocalRandom.current();
  private final UserGenerator userGen = new UserGenerator();

  /** Creates an Admin for an existing persisted user (user.id must be non-null). */
  public Admin createForExistingUser(User persistedUser) {
    Objects.requireNonNull(persistedUser, "user is required");
    if (persistedUser.getId() == null) {
      throw new IllegalArgumentException("persistedUser.id must be non-null");
    }
    return Admin.builder().user(persistedUser).grantedAt(randomPastInstant()).build();
  }

  /** Creates an Admin and a persisted-like user (use when you just need a valid aggregate). */
  public Admin createWithNewPersistedUser() {
    User u = userGen.createRandomPersistedUser();
    return Admin.builder().user(u).grantedAt(randomPastInstant()).build();
  }

  private OffsetDateTime randomPastInstant() {
    return OffsetDateTime.now().minusSeconds(rnd.nextLong(60, 3600));
  }
}
