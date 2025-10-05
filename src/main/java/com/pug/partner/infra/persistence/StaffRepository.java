package com.pug.partner.infra.persistence;

import com.pug.partner.domain.Staff;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StaffRepository implements PanacheRepository<Staff> {}
