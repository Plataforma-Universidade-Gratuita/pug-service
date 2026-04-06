package com.pug.academic.infra.read.dtos;

import com.pug.academic.infra.persistence.CourseEntity;
import com.pug.academic.infra.persistence.SchoolEntity;
import com.pug.academic.infra.persistence.StudentEntity;
import com.pug.identity.infra.persistence.AccountEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Internal Data Transfer Object (DTO) used exclusively for JPA tuple projections.
 *
 * <p>This record acts as an intermediate data structure during complex cross-domain queries. By
 * fetching the {@link StudentEntity} and all its required associations (Account, Course, School) in
 * a single query projection, it prevents N+1 select performance issues before the data is
 * ultimately mapped into the final, flattened, client-facing {@link
 * com.pug.academic.infra.read.dtos.StudentView}.
 *
 * @param s the retrieved student persistence entity
 * @param acc the retrieved authentication account persistence entity linked to the student
 * @param c the retrieved course persistence entity linked to the student
 * @param sch the retrieved school persistence entity linked to the course
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record StudentAcc(StudentEntity s, AccountEntity acc, CourseEntity c, SchoolEntity sch) {}
