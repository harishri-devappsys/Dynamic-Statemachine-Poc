package com.flexflow.statemachine.model.db;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA Entity to track the current state of any business object
 * that is managed by a dynamic state machine.
 */
@Entity
@Table(name = "stateful_entities", indexes = {
        @Index(name = "idx_business_id", columnList = "businessId")
})
@Getter
@Setter
public class StatefulEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The ID of the business object being tracked (e.g., a clarificationId, orderId, etc.).
     * This should be unique in the context of your application domain.
     */
    @Column(name = "business_id", nullable = false)
    private String businessId;

    /**
     * The machineId of the StateMachineDefinition that governs this entity's lifecycle.
     * This creates a link to the specific version of the state machine logic.
     */
    @Column(name = "machine_id", nullable = false)
    private String machineId;

    /**
     * The current state of the business object within its lifecycle.
     */
    @Column(name = "current_state", nullable = false)
    private String currentState;

    /**
     * Optimistic locking field to prevent concurrent modification issues.
     */
    @Version
    private Integer version;
}