package org.athena.framework.data.jdbc.web;


import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.entity.dto.IDTO;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.vo.PageResultVO;
import org.athena.framework.web.annotation.web.ApiDeleteMapping;
import org.athena.framework.web.annotation.web.ApiGetMapping;
import org.athena.framework.web.annotation.web.ApiPostMapping;
import org.athena.framework.web.annotation.web.ApiPutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Interface defining the contract for a controller in the application.
 * Implementations of this interface are responsible for handling
 * business logic and coordinating between the model and view components.
 * It acts as an intermediary to manage data flow, user input, and updates
 * to the user interface.
 */
public interface IController<
        Entity extends IEntity,
        DTO extends IDTO,
        Query extends BaseRequest> {

    /**
     * Adds a new entity to the system based on the provided DTO.
     *
     * @param dto the data transfer object containing the details of the entity to be added
     * @return the DTO representing the newly added entity, including any generated or updated fields such as ID
     */
    @ApiPostMapping
    DTO add(DTO dto);

    /**
     * Updates an existing entity in the system based on the provided DTO and ID.
     *
     * @param id the unique identifier of the entity to be updated
     * @param dto the data transfer object containing the updated details of the entity
     * @return the DTO representing the updated entity, including any generated or updated fields such as version
     */
    @ApiPutMapping("/{id}")
    DTO update(@PathVariable("id") Long id, @RequestBody DTO dto);

    /**
     * Partially updates an existing entity in the system based on the provided DTO and ID.
     *
     * @param id the unique identifier of the entity to be partially updated
     * @param dto the data transfer object containing the partial updates for the entity
     * @return the DTO representing the partially updated entity, including any generated or updated fields such as version
     */
    @PatchMapping("/{id}")
    DTO edit(@PathVariable("id") Long id, @RequestBody DTO dto);

    /**
     * Deletes an entity from the system based on the provided ID.
     *
     * @param id the unique identifier of the entity to be deleted
     * @return true if the deletion was successful, false otherwise
     */
    @ApiDeleteMapping("/{id}")
    Boolean delete(@PathVariable("id") Long id);

    /**
     * Retrieves a paginated list of DTOs based on the provided query parameters.
     *
     * @param query the query object containing pagination and filtering criteria
     * @return a PageResultVO containing a list of DTOs that match the query, along with pagination information
     */
    @ApiGetMapping
    PageResultVO<DTO> page(Query query);

    /**
     * Retrieves a specific entity's DTO based on the provided ID.
     *
     * @param id the unique identifier of the entity to be retrieved
     * @return the DTO representing the retrieved entity
     */
    @ApiGetMapping("/{id}")
    DTO get(@PathVariable("id") Long id);

}
