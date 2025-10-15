package com.pug.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminRepository {
  boolean isAdmin(UUID userId);

  void grant(UUID userId);

  void revoke(UUID userId);

  Optional<Admin> findByUserId(UUID userId);

  List<Admin> listAllAdmins();
}
