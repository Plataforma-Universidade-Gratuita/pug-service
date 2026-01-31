package com.pug.academic.infra.read.dtos;

import com.pug.academic.infra.persistence.CourseEntity;
import com.pug.academic.infra.persistence.SchoolEntity;
import com.pug.academic.infra.persistence.StudentEntity;
import com.pug.identity.infra.persistence.AccountEntity;

public record StudentAcc(StudentEntity s, AccountEntity acc, CourseEntity c, SchoolEntity sch) {
}