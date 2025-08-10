package com.flexflow.statemachine.service.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexflow.statemachine.model.db.StateMachineDefinition;
import com.flexflow.statemachine.model.json.StateMachineDefinitionJson;
import com.flexflow.statemachine.model.json.TransitionJson;
import com.flexflow.statemachine.repository.StateMachineDefinitionRepository;
import com.flexflow.statemachine.service.registry.ActionRegistry;
import com.flexflow.statemachine.service.registry.GuardRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Builds and caches StateMachineFactory instances from JSON definitions stored in the database.
 * This service is the heart of the dynamic state machine system, responsible for runtime configuration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StateMachineBuilderFactory {

    private final StateMachineDefinitionRepository definitionRepository;
    private final ActionRegistry actionRegistry;
    private final GuardRegistry guardRegistry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ConcurrentHashMap<String, StateMachineFactory<String, String>> factoryCache = new ConcurrentHashMap<>();

    public StateMachineFactory<String, String> getFactory(String machineId) {
        return factoryCache.computeIfAbsent(machineId, this::buildFactory);
    }

    private StateMachineFactory<String, String> buildFactory(String machineId) {
        log.info("Cache miss for machineId: '{}'. Building new StateMachineFactory.", machineId);

        StateMachineDefinition definition = definitionRepository.findByMachineId(machineId)
                .orElseThrow(() -> new IllegalArgumentException("No state machine definition found for machineId: " + machineId));

        try {
            StateMachineDefinitionJson jsonDefinition = objectMapper.readValue(
                    definition.getDefinitionJson(),
                    StateMachineDefinitionJson.class
            );

            // Return a factory that builds new machines from json on demand.
            return new StateMachineFactory<>() {
                @Override
                public StateMachine<String, String> getStateMachine() {
                    try {
                        return buildStateMachine(jsonDefinition);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to build state machine for machineId: " + machineId, e);
                    }
                }

                @Override
                public StateMachine<String, String> getStateMachine(String id) {
                    return getStateMachine();
                }

                @Override
                public StateMachine<String, String> getStateMachine(UUID uuid) {
                    return getStateMachine();
                }
            };

        } catch (Exception e) {
            log.error("Failed to build StateMachineFactory for machineId: {}", machineId, e);
            throw new RuntimeException("Failed to build state machine factory for " + machineId, e);
        }
    }

    /**
     * This helper method contains the actual logic for building a single StateMachine instance.
     * @param jsonDefinition The parsed JSON definition.
     * @return A fully configured StateMachine.
     * @throws Exception if configuration fails.
     */
    private StateMachine<String, String> buildStateMachine(StateMachineDefinitionJson jsonDefinition) throws Exception {
        StateMachineBuilder.Builder<String, String> builder = StateMachineBuilder.builder();

        // 1. Configure States
        Set<String> states = jsonDefinition.getStates().stream().collect(Collectors.toSet());
        builder.configureStates()
                .withStates()
                .initial(jsonDefinition.getInitialState())
                .states(states);

        if (jsonDefinition.getEndStates() != null && !jsonDefinition.getEndStates().isEmpty()) {
            // Re-get the configurer to add the end state.
            builder.configureStates().withStates().end(jsonDefinition.getEndStates().get(0));
        }

        // 2. Configure Transitions
        StateMachineTransitionConfigurer<String, String> transitions = builder.configureTransitions();
        for (TransitionJson transition : jsonDefinition.getTransitions()) {
            transitions
                    .withExternal()
                    .source(transition.getSource())
                    .target(transition.getTarget())
                    .event(transition.getEvent())
                    .guard(context -> {
                        if (transition.getGuard() == null || transition.getGuard().getName() == null) {
                            return true; // No guard defined, so allow transition.
                        }
                        return guardRegistry.getGuard(transition.getGuard().getName())
                                .map(guard -> guard.evaluate(
                                        context,
                                        transition.getGuard().getParams() != null
                                                ? transition.getGuard().getParams()
                                                : Collections.emptyMap()
                                ))
                                .orElseThrow(() -> new RuntimeException("Guard not found: " + transition.getGuard().getName()));
                    })
                    .action(context -> {
                        if (transition.getAction() != null && transition.getAction().getName() != null) {
                            actionRegistry.getAction(transition.getAction().getName())
                                    .ifPresentOrElse(
                                            action -> action.execute(
                                                    context,
                                                    transition.getAction().getParams() != null
                                                            ? transition.getAction().getParams()
                                                            : Collections.emptyMap()
                                            ),
                                            () -> {
                                                throw new RuntimeException("Action not found: " + transition.getAction().getName());
                                            }
                                    );
                        }
                    })
                    .and(); //finalize and prepare for next
        }

        log.debug("State machine built successfully for initialState='{}'", jsonDefinition.getInitialState());
        return builder.build();
    }

    public void clearCache(String machineId) {
        log.info("Clearing cache for machineId: '{}'", machineId);
        factoryCache.remove(machineId);
    }
}
