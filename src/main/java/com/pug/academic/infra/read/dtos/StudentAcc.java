package com.pug.academic.infra.read.dtos;

import com.pug.academic.infra.persistence.CourseEntity;
import com.pug.academic.infra.persistence.SchoolEntity;
import com.pug.academic.infra.persistence.StudentEntity;
import com.pug.identity.infra.persistence.AccountEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Record representing a combination of StudentEntity, AccountEntity, CourseEntity, and
 * SchoolEntity, typically used in JPA projections to simplify data retrieval.
 *
 * @param s the StudentEntity.
 * @param acc the AccountEntity.
 * @param c the CourseEntity.
 * @param sch the SchoolEntity.
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record StudentAcc(StudentEntity s, AccountEntity acc, CourseEntity c, SchoolEntity sch) {}
