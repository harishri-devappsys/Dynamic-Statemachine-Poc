package com.flexflow.statemachine.service.core;

import com.flexflow.statemachine.model.db.StatefulEntity;
import com.flexflow.statemachine.repository.StatefulEntityRepository;
import com.flexflow.statemachine.service.persister.DynamicStateMachinePersister;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

/**
 * The main service for interacting with the dynamic state machine system.
 * This class orchestrates loading machines, restoring state, sending events, and persisting results.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicStateMachineService {

    private final StateMachineBuilderFactory builderFactory;
    private final StatefulEntityRepository entityRepository;
    private final DynamicStateMachinePersister persister;

    /**
     * Creates a new stateful entity and persists its initial state.
     *
     * @param businessId The unique ID for the business object.
     * @param machineId  The ID of the state machine definition to use.
     * @return The newly created StatefulEntity.
     * @throws Exception if the machine definition cannot be found or parsed.
     */
    @Transactional
    public StatefulEntity createEntity(String businessId, String machineId) throws Exception {
        if (entityRepository.findByBusinessId(businessId).isPresent()) {
            throw new IllegalArgumentException("Entity with businessId '" + businessId + "' already exists.");
        }

        StateMachineFactory<String, String> factory = builderFactory.getFactory(machineId);
        StateMachine<String, String> stateMachine = factory.getStateMachine();

        StatefulEntity newEntity = new StatefulEntity();
        newEntity.setBusinessId(businessId);
        newEntity.setMachineId(machineId);
        newEntity.setCurrentState(stateMachine.getInitialState().getId());

        log.info("Creating new entity with businessId '{}', machineId '{}', initialState '{}'",
                businessId, machineId, newEntity.getCurrentState());

        return entityRepository.save(newEntity);
    }

    /**
     * Triggers an event on a specific stateful entity.
     *
     * @param businessId The ID of the entity to trigger the event on.
     * @param event      The event to trigger.
     * @param headers    A map of headers to pass to the state machine context (for guards/actions).
     * @return true if the event was accepted AND resulted in a state change, false otherwise.
     */
    @Transactional
    public boolean triggerEvent(String businessId, String event, Map<String, Object> headers) {
        log.info("Attempting to trigger event '{}' for entity '{}'", event, businessId);
        try {
            StatefulEntity entity = entityRepository.findByBusinessId(businessId)
                    .orElseThrow(() -> new IllegalArgumentException("Entity not found: " + businessId));

            StateMachineFactory<String, String> factory = builderFactory.getFactory(entity.getMachineId());
            StateMachine<String, String> stateMachine = factory.getStateMachine();

            DefaultStateMachinePersister<String, String, String> smp = new DefaultStateMachinePersister<>(persister);
            smp.restore(stateMachine, businessId);

            // 1. Get the state before the event is sent.
            State<String, String> beforeState = stateMachine.getState();

            MessageBuilder<String> messageBuilder = MessageBuilder
                    .withPayload(event)
                    .setHeader("entityId", businessId);

            if (headers != null) {
                headers.forEach(messageBuilder::setHeader);
            }

            // 2. Send the event.
            stateMachine.sendEvent(messageBuilder.build());

            // 3. Get the state AFTER the event.
            State<String, String> afterState = stateMachine.getState();

            // 4. Compare the states. Only persist if the state has actually changed.
            boolean stateChanged = !Objects.equals(beforeState.getId(), afterState.getId());

            if (stateChanged) {
                log.info("Event '{}' accepted for entity '{}'. Persisting new state.", event, businessId);
                smp.persist(stateMachine, businessId);
            } else {
                log.warn("Event '{}' was handled but did not result in a state change for entity '{}'. Current state: {}",
                        event, businessId, stateMachine.getState().getId());
            }

            return stateChanged;

        } catch (Exception e) {
            log.error("Error triggering event for businessId: {}", businessId, e);
            throw new RuntimeException("Failed to trigger event for " + businessId, e);
        }
    }
}