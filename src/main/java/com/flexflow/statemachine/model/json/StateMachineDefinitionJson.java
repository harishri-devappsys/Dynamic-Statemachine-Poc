package com.flexflow.statemachine.model.json;

import lombok.Data;
import java.util.List;

/**
 * Represents the root object of the state machine definition JSON.
 * This class is the target for deserializing the entire configuration file.
 */
@Data
public class StateMachineDefinitionJson {
    /**
     * A unique identifier for this state machine definition (e.g., "clarification_lifecycle_v1").
     */
    private String machineId;

    /**
     * The initial state of the machine when it's first created.
     */
    private String initialState;

    /**
     * A list of all possible states in the machine.
     */
    private List<String> states;

    /**
     * A list of terminal states. Once in an end state, no more events are processed.
     */
    private List<String> endStates;

    /**
     * The list of all transitions that define the machine's behavior.
     */
    private List<TransitionJson> transitions;
}