package com.pug.project.service.impl;

import com.pug.project.service.EnrollmentService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EnrollmentServiceImpl implements EnrollmentService {

  @Override
  public boolean existsAnyByStudentId(java.util.UUID accountId) {
    return false;
  }
}
