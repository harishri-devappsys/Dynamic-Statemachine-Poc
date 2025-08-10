package com.flexflow.statemachine.repository;

import com.flexflow.statemachine.model.db.StateMachineDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for StateMachineDefinition entities.
 */
@Repository
public interface StateMachineDefinitionRepository extends JpaRepository<StateMachineDefinition, Long> {

    /**
     * Finds a state machine definition by its unique machineId.
     * This is the primary method for retrieving a specific state machine's configuration.
     *
     * @param machineId The unique identifier of the state machine.
     * @return An Optional containing the definition if found.
     */
    Optional<StateMachineDefinition> findByMachineId(String machineId);
}