package com.flexflow.statemachine.repository;

import com.flexflow.statemachine.model.db.StatefulEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for StatefulEntity entities.
 */
@Repository
public interface StatefulEntityRepository extends JpaRepository<StatefulEntity, Long> {

    /**
     * Finds the stateful entity record for a given business object ID.
     *
     * @param businessId The unique ID of the business object (e.g., clarificationId).
     * @return An Optional containing the stateful entity if found.
     */
    Optional<StatefulEntity> findByBusinessId(String businessId);
}
