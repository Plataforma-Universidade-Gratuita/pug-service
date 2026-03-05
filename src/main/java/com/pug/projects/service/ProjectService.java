package com.pug.projects.service;

import java.util.UUID;

public interface ProjectService {

  boolean existsAnyByEntityId(UUID entityId);

  boolean existsByCreatedBy(UUID accountId);
}
