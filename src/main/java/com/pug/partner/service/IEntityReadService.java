package com.pug.partner.service;

import com.pug.partner.infra.read.dtos.EntityView;

import java.util.List;
import java.util.UUID;

/**
 * Interface for reading entity information.
 */
public interface IEntityReadService {

    /**
     * Retrieves an EntityView by its ID.
     *
     * @param id the UUID of the entity
     * @return the EntityView
     * @throws com.pug.shared.exceptions.ResourceNotFoundException if no entity with the given ID is found
     */
    EntityView getViewById(UUID id);

    /**
     * Retrieves an EntityView by its CNPJ.
     *
     * @param cnpj the CNPJ of the entity
     * @return the EntityView
     * @throws com.pug.shared.exceptions.ResourceNotFoundException if no entity with the given CNPJ is found
     */
    EntityView getViewByCnpj(String cnpj);

    /**
     * Lists all EntityViews.
     *
     * @return a list of all EntityViews
     */
    List<EntityView> listViews();

    /**
     * Lists EntityViews by city ID.
     *
     * @param cityId the UUID of the city
     * @return a list of EntityViews in the specified city
     */
    List<EntityView> listViewsByCityId(UUID cityId);

    /**
     * Searches for EntityViews by name.
     *
     * @param query the search query
     * @return a list of matching EntityViews
     */
    List<EntityView> searchViews(String query);
}