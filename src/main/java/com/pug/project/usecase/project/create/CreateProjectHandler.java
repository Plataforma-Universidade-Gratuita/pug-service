package com.pug.project.usecase.project.create;

import com.pug.academic.domain.FieldOfStudy;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.Staff;
import com.pug.project.domain.Project;
import com.pug.project.infra.persistence.ProjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ApplicationScoped
public class CreateProjectHandler {

    @Inject ProjectRepository repo;
    @Inject EntityManager em;
    @Inject Validator validator;

    @Transactional
    public java.util.UUID handle(CreateProjectCommand cmd) {
        final String name = cmd.name() == null ? null : cmd.name().trim();

        var entityStub = cmd.entityId() == null ? null : PartnerEntity.builder().id(cmd.entityId()).build();
        var fieldStub  = cmd.fieldId()  == null ? null : FieldOfStudy.builder().id(cmd.fieldId()).build();
        var staffStub  = cmd.createdById() == null ? null : Staff.builder().id(cmd.createdById()).build();

        var candidate =
                Project.builder()
                        .name(name)
                        .description(cmd.description())
                        .entity(entityStub)
                        .field(fieldStub)
                        .maxParticipants(cmd.maxParticipants())
                        .createdBy(staffStub)
                        .updatedBy(staffStub)
                        .build();

        var v = validator.validate(candidate);
        if (!v.isEmpty()) throw new ConstraintViolationException(v);

        candidate.setEntity(em.getReference(PartnerEntity.class, cmd.entityId()));
        candidate.setField(em.getReference(FieldOfStudy.class, cmd.fieldId()));
        var staffRef = em.getReference(Staff.class, cmd.createdById());
        candidate.setCreatedBy(staffRef);
        candidate.setUpdatedBy(staffRef);

        repo.persist(candidate);
        repo.flush();
        return candidate.getId();
    }
}
