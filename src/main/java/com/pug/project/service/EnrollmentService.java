package com.pug.project.service;

import java.util.UUID;

public interface EnrollmentService {
  boolean existsAnyByStudentId(UUID accountId);
}
