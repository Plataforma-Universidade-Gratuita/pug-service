package com.pug.project.service.impl;

import com.pug.project.service.AttendanceService;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class AttendanceServiceImpl implements AttendanceService {

  @Override
  public boolean existsByValidatedBy(UUID accountId) {
    return false;
  }
}
