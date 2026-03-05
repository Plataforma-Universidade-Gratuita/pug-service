package com.pug.projects.infra.read.impl;

import com.pug.projects.infra.read.EnrollmentQueries;
import com.pug.projects.infra.read.dtos.EnrollmentView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link EnrollmentQueries} interface.
 *
 * <p>This massive JPQL constructor projection maps an Enrollment alongside the full Project graph
 * (Project -> Entity -> City) and the full Student graph (Student -> Account -> User, Student ->
 * Course -> School) in a single database hit.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class EnrollmentQueriesImpl implements EnrollmentQueries {

  @Inject EntityManager em;

  private static final String SELECT_BASE =
      """
                    select new com.pug.projects.infra.read.dtos.EnrollmentView(
                      new com.pug.projects.infra.read.dtos.ProjectView(
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
                      en.status, en.createdAt, en.updatedAt, en.acceptedAt, en.closingStatusAt
                    )
                    from EnrollmentEntity en
                    join ProjectEntity p on p.id = en.id.projectId
                    join EntityEntity ent on ent.id = p.entityId
                    join CityEntity c on c.id = ent.cityId
                    join AccountEntity pacc on pacc.id = p.createdBy
                    join UserEntity pu on pu.id = pacc.userId
                    join StudentEntity s on s.accountId = en.id.studentId
                    join AccountEntity sacc on sacc.id = s.accountId
                    join UserEntity su on su.id = sacc.userId
                    join CourseEntity course on course.id = s.courseId
                    left join SchoolEntity sch on sch.id = course.schoolId
                    """;

  private static final String ORDER_BY_DATE = " order by en.createdAt desc";

  @Override
  public Optional<EnrollmentView> findOptionalByIds(UUID projectId, UUID studentId) {
    if (projectId == null || studentId == null) return Optional.empty();
    var q =
        em.createQuery(
            SELECT_BASE + " where en.id.projectId = :pid and en.id.studentId = :sid",
            EnrollmentView.class);
    q.setParameter("pid", projectId);
    q.setParameter("sid", studentId);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<EnrollmentView> listAllEnrollments() {
    return em.createQuery(SELECT_BASE + ORDER_BY_DATE, EnrollmentView.class).getResultList();
  }

  @Override
  public List<EnrollmentView> listByProjectId(UUID projectId) {
    if (projectId == null) return List.of();
    var q =
        em.createQuery(
            SELECT_BASE + " where en.id.projectId = :pid" + ORDER_BY_DATE, EnrollmentView.class);
    q.setParameter("pid", projectId);
    return q.getResultList();
  }

  @Override
  public List<EnrollmentView> listByStudentId(UUID studentId) {
    if (studentId == null) return List.of();
    var q =
        em.createQuery(
            SELECT_BASE + " where en.id.studentId = :sid" + ORDER_BY_DATE, EnrollmentView.class);
    q.setParameter("sid", studentId);
    return q.getResultList();
  }
}
