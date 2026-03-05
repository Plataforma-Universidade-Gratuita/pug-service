package com.pug.projects.service;

import java.util.UUID;

public interface EnrollmentService {
  boolean existsAnyByStudentId(UUID accountId);
}
