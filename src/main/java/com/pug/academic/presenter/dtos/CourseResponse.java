package com.pug.academic.presenter.dtos;

import java.util.UUID;

public record CourseResponse(UUID id, String name, SchoolResponse school) {}
