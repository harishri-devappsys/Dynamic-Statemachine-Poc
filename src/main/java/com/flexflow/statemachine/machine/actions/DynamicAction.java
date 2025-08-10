package com.flexflow.statemachine.machine.actions;

import org.springframework.statemachine.StateContext;
import java.util.Map;

/**
 * Interface for all dynamic actions that can be executed during a state transition.
 * Implementations of this interface should be Spring beans (@Component).
 */
public interface DynamicAction {

    /**
     * A unique name for the action. This name is used in the JSON definition
     * to reference this specific action implementation.
     *
     * @return The unique name of the action bean.
     */
    String getName();

    /**
     * The business logic to be executed when this action is triggered.
     *
     * @param context The state context, providing access to the state machine, event, headers, etc.
     * @param params  A map of parameters as defined in the 'params' block of the action in the JSON definition.
     */
    void execute(StateContext<String, String> context, Map<String, Object> params);
}