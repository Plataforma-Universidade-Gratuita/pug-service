package com.pug.project.presenter.rest.dto;

public record ProjectAllocationRequest(
    String projectId, String offeredHours, String startDate, String endDate) {}
