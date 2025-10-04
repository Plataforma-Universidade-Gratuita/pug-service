package com.pug.hours.presenter.rest.dto;

public record HoursRequirementRequest(
    String studentId, String requiredHours, String startDate, String dueDate) {}
