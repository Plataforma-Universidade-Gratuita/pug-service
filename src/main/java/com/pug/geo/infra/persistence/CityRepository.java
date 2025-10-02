package com.pug.geo.infra.persistence;

import com.pug.geo.domain.City;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
@ApplicationScoped
public class CityRepository implements PanacheRepository<City> {}
