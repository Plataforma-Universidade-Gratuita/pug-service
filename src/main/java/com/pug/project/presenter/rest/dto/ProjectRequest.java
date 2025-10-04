package com.pug.project.presenter.rest.dto;

public record ProjectRequest(
    String name, String description, String entityId, String fieldId, String status) {}
