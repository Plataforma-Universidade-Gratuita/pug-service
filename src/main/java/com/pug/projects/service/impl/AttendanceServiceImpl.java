package com.pug.projects.service.impl;

import com.pug.projects.service.AttendanceService;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class AttendanceServiceImpl implements AttendanceService {

  @Override
  public boolean existsByValidatedBy(UUID accountId) {
    return false;
  }
}
