package com.pug.project.infra.read.impl;

import com.pug.project.infra.read.AttendanceQueries;
import com.pug.project.infra.read.dtos.AttendanceView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link AttendanceQueries} interface.
 *
 * <p>Relies on a massive JPQL constructor expression to flatten out the attendance data along with
 * the fully populated Project graph, Student graph, and Validator Account.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class AttendanceQueriesImpl implements AttendanceQueries {

  @Inject EntityManager em;

  private static final String SELECT_BASE =
      """
                    select new com.pug.project.infra.read.dtos.AttendanceView(
                      a.id,
                      new com.pug.project.infra.read.dtos.ProjectView(
                        p.id, p.name,
                        new com.pug.partner.infra.read.dtos.EntityView(
                          ent.id, ent.cnpj, ent.name, ent.address,
                          new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode),
                          ent.createdAt, ent.updatedAt
                        ),
                        p.description,
                        new com.pug.identity.infra.read.dtos.AccountView(
                          pacc.id,
                          new com.pug.identity.infra.read.dtos.UserView(
                            pu.id, pu.cpf, pu.name, pu.createdAt, pu.updatedAt
                          ),
                          pacc.email, pacc.accountType, pacc.createdAt, pacc.updatedAt, pacc.active
                        ),
                        p.maxParticipants, p.offeredHours, p.status, p.closedAt, p.createdAt, p.updatedAt
                      ),
                      new com.pug.academic.infra.read.dtos.StudentView(
                        new com.pug.identity.infra.read.dtos.AccountView(
                          sacc.id,
                          new com.pug.identity.infra.read.dtos.UserView(
                            su.id, su.cpf, su.name, su.createdAt, su.updatedAt
                          ),
                          sacc.email, sacc.accountType, sacc.createdAt, sacc.updatedAt, sacc.active
                        ),
                        s.academicRegistration, s.campus,
                        new com.pug.academic.infra.read.dtos.CourseView(
                          course.id, course.name,
                          new com.pug.academic.infra.read.dtos.SchoolView(
                            sch.id, sch.name, sch.createdAt, sch.updatedAt
                          ),
                          course.createdAt, course.updatedAt
                        ),
                        s.requiredHours, s.concluded, s.startDate, s.dueDate, s.createdAt, s.updatedAt
                      ),
                      a.duration, a.latitude, a.longitude, a.qrValidationHash, a.status,
                      new com.pug.identity.infra.read.dtos.AccountView(
                        vacc.id,
                        new com.pug.identity.infra.read.dtos.UserView(
                          vu.id, vu.cpf, vu.name, vu.createdAt, vu.updatedAt
                        ),
                        vacc.email, vacc.accountType, vacc.createdAt, vacc.updatedAt, vacc.active
                      ),
                      a.validatedAt, a.createdAt, a.updatedAt
                    )
                    from AttendanceEntity a
                    join ProjectEntity p on p.id = a.projectId
                    join EntityEntity ent on ent.id = p.entityId
                    join CityEntity c on c.id = ent.cityId
                    join AccountEntity pacc on pacc.id = p.createdBy
                    join UserEntity pu on pu.id = pacc.userId
                    join StudentEntity s on s.accountId = a.studentId
                    join AccountEntity sacc on sacc.id = s.accountId
                    join UserEntity su on su.id = sacc.userId
                    join CourseEntity course on course.id = s.courseId
                    left join SchoolEntity sch on sch.id = course.schoolId
                    left join AccountEntity vacc on vacc.id = a.validatedBy
                    left join UserEntity vu on vu.id = vacc.userId
                    """;

  private static final String ORDER_BY_DATE = " order by a.createdAt desc";

  @Override
  public Optional<AttendanceView> findOptionalById(UUID id) {
    if (id == null) return Optional.empty();
    var q = em.createQuery(SELECT_BASE + " where a.id = :id", AttendanceView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<AttendanceView> listAllAttendances() {
    return em.createQuery(SELECT_BASE + ORDER_BY_DATE, AttendanceView.class).getResultList();
  }

  @Override
  public List<AttendanceView> listByProjectId(UUID projectId) {
    if (projectId == null) return List.of();
    var q =
        em.createQuery(
            SELECT_BASE + " where a.projectId = :pid" + ORDER_BY_DATE, AttendanceView.class);
    q.setParameter("pid", projectId);
    return q.getResultList();
  }

  @Override
  public List<AttendanceView> listByStudentId(UUID studentId) {
    if (studentId == null) return List.of();
    var q =
        em.createQuery(
            SELECT_BASE + " where a.studentId = :sid" + ORDER_BY_DATE, AttendanceView.class);
    q.setParameter("sid", studentId);
    return q.getResultList();
  }
}
