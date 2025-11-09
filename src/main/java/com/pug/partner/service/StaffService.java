package com.pug.partner.service;

import com.pug.identity.service.UsersService;
import com.pug.partner.domain.Entity;
import com.pug.partner.domain.Staff;
import com.pug.partner.domain.StaffRepository;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Service for managing staff assignments to partner entities.
 */
@ApplicationScoped
public class StaffService {

    @Inject
    StaffRepository repo;
    @Inject
    UsersService usersService;
    @Inject
    EntitiesService entitiesService;

    /**
     * Assign a user as staff to a specific entity.
     * @param userId the ID of the user to be assigned as staff.
     * @param entityId the ID of the entity to which the user is assigned.
     * @return the created Staff record.
     * @throws DuplicateResourceException if the user is already assigned as staff.
     */
    @Transactional
    public Staff assign(UUID userId, UUID entityId) {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(entityId, "entityId");

        if (repo.existsByUserId(userId)) {
            throw new DuplicateResourceException(PartnerErrorCodes.STAFF_ALREADY_EXISTS);
        }

        var user = usersService.getById(userId);
        Entity entity = entitiesService.getById(entityId);

        var staff = Staff.builder().user(user).entity(entity).build();
        return repo.persist(staff);
    }

    /**
     * Assign multiple users as staff to a specific entity.
     * @param entityId the ID of the entity to which the users are assigned.
     * @param userIds the IDs of the users to be assigned as staff.
     * @return the list of created Staff records.
     * @throws DuplicateResourceException if any user is already assigned as staff.
     */
    @Transactional
    public List<Staff> assignAll(UUID entityId, Iterable<UUID> userIds) {
        Objects.requireNonNull(entityId, "entityId");
        Objects.requireNonNull(userIds, "userIds");

        Entity entity = entitiesService.getById(entityId);

        Set<UUID> uniqueIds = new LinkedHashSet<>();
        for (UUID id : userIds) {
            if (id != null) uniqueIds.add(id);
        }
        if (uniqueIds.isEmpty()) {
            return List.of();
        }

        List<Staff> toPersist = new ArrayList<>(uniqueIds.size());
        for (UUID uid : uniqueIds) {
            if (repo.existsByUserId(uid)) {
                throw new DuplicateResourceException(PartnerErrorCodes.STAFF_ALREADY_EXISTS);
            }
            var user = usersService.getById(uid);
            toPersist.add(Staff.builder().user(user).entity(entity).build());
        }

        return repo.persistAll(toPersist);
    }

    /**
     * Revoke staff assignment for a user.
     * @param userId the ID of the user whose staff assignment is to be revoked.
     * @throws ResourceNotFoundException if the staff assignment does not exist.
     */
    @Transactional
    public void revoke(UUID userId) {
        Objects.requireNonNull(userId, "userId");
        repo.findOptionalByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND));
        repo.deleteByUserIds(List.of(userId));
    }

    /**
     * Get staff details by user ID.
     * @param userId the ID of the user.
     * @return the Staff record.
     * @throws ResourceNotFoundException if the staff assignment does not exist.
     */
    public Staff get(UUID userId) {
        Objects.requireNonNull(userId, "userId");
        return repo.findOptionalByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND));
    }

    /**
     * List all staff records.
     * @return the list of all Staff records.
     */
    public List<Staff> listAll() {
        return repo.listAllStaff();
    }

    /**
     * List all staff records for a specific entity.
     * @param entityId the ID of the entity.
     * @return the list of Staff records for the entity.
     */
    public List<Staff> listByEntity(UUID entityId) {
        Objects.requireNonNull(entityId, "entityId");
        return repo.listAllByEntityId(entityId);
    }

    /**
     * Check if a staff assignment exists for a user.
     * @param userId the ID of the user.
     * @return true if the staff assignment exists, false otherwise.
     */
    public boolean existsByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId");
        return repo.existsByUserId(userId);
    }
}
