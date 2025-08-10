package com.flexflow.statemachine.model.db;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA Entity to store the raw JSON definition of a state machine in the database.
 */
@Entity
@Table(name = "state_machine_definitions")
@Getter
@Setter
public class StateMachineDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique business key for the state machine definition.
     * This is used to look up the definition at runtime.
     * Example: "clarification_lifecycle_v1"
     */
    @Column(name = "machine_id", unique = true, nullable = false)
    private String machineId;

    /**
     * The complete state machine definition stored as a JSON string.
     * Using TEXT type for flexibility and to accommodate large definitions.
     */
    @Lob
    @Column(name = "definition_json", nullable = false, columnDefinition = "TEXT")
    private String definitionJson;
}