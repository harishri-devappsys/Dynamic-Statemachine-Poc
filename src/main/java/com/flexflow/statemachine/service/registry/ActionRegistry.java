package com.flexflow.statemachine.service.registry;

import com.flexflow.statemachine.machine.actions.DynamicAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A registry that discovers and holds all DynamicAction implementations from the Spring context.
 * This allows for looking up an action by its name as specified in the JSON definition.
 */
@Service
public class ActionRegistry {

    private final Map<String, DynamicAction> actionMap;

    /**
     * On startup, Spring injects all beans that implement DynamicAction.
     * We then map them by their unique name for quick lookups.
     */
    @Autowired
    public ActionRegistry(List<DynamicAction> actions) {
        this.actionMap = actions.stream()
                .collect(Collectors.toMap(DynamicAction::getName, Function.identity()));
    }

    public Optional<DynamicAction> getAction(String name) {
        return Optional.ofNullable(actionMap.get(name));
    }
}