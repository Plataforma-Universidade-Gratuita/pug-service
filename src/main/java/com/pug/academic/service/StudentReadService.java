// src/main/java/com/pug/academic/service/StudentReadService.java
package com.pug.academic.service;

import com.pug.academic.infra.queries.StudentQueries;
import com.pug.academic.presenter.dtos.StudentView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class StudentReadService {
    @Inject
    StudentQueries queries;

    public StudentView getView(UUID id) {
        return queries.findById(id).orElse(null);
    }

    public List<StudentView> listViews() {
        return queries.listAll();
    }

    public List<StudentView> listViewsByCourseId(UUID courseId) {
        return queries.listAllByCourseId(courseId);
    }
}
