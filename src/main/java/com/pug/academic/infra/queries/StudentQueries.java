package com.pug.academic.infra.queries;

import com.pug.academic.presenter.dtos.StudentView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentQueries {
    Optional<StudentView> findById(UUID userId);

    List<StudentView> listAll();

    List<StudentView> listAllByCourseId(UUID courseId);
}
