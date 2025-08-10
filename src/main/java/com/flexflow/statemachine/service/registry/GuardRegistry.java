package com.flexflow.statemachine.service.registry;

import com.flexflow.statemachine.machine.guards.DynamicGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A registry that discovers and holds all DynamicGuard implementations from the Spring context.
 * This allows for looking up a guard by its name as specified in the JSON definition.
 */
@Service
public class GuardRegistry {

    private final Map<String, DynamicGuard> guardMap;

    /**
     * On startup, Spring injects all beans that implement DynamicGuard.
     * We then map them by their unique name for quick lookups.
     */
    @Autowired
    public GuardRegistry(List<DynamicGuard> guards) {
        this.guardMap = guards.stream()
                .collect(Collectors.toMap(DynamicGuard::getName, Function.identity()));
    }

    public Optional<DynamicGuard> getGuard(String name) {
        return Optional.ofNullable(guardMap.get(name));
    }
}