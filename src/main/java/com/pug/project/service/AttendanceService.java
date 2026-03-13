package com.pug.project.service;

import java.util.UUID;

public interface AttendanceService {

  boolean existsByValidatedBy(UUID accountId);
}
