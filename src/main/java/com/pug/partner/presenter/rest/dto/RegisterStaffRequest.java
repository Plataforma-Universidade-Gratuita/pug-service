package com.pug.partner.presenter.rest.dto;

import java.util.UUID;

public record RegisterStaffRequest(UUID userRoleId, UUID entityId) {}
