package com.flexflow.statemachine.model.json;

import lombok.Data;

/**
 * Represents a single transition within the state machine definition JSON.
 */
@Data
public class TransitionJson {
    private String source;
    private String target;
    private String event;
    private ActionGuardJson action;
    private ActionGuardJson guard;
}
