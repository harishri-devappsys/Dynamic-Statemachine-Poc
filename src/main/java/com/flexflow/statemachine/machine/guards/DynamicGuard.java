package com.flexflow.statemachine.machine.guards;

import org.springframework.statemachine.StateContext;
import java.util.Map;

/**
 * Interface for all dynamic guards that can evaluate whether a transition should proceed.
 * Implementations of this interface should be Spring beans (@Component).
 */
public interface DynamicGuard {

    /**
     * A unique name for the guard. This name is used in the JSON definition
     * to reference this specific guard implementation.
     *
     * @return The unique name of the guard bean.
     */
    String getName();

    /**
     * The logic to be evaluated to determine if the transition is allowed.
     *
     * @param context The state context, providing access to the state machine, event, headers, etc.
     * @param params  A map of parameters as defined in the 'params' block of the guard in the JSON definition.
     * @return true if the transition is allowed, false otherwise.
     */
    boolean evaluate(StateContext<String, String> context, Map<String, Object> params);
}