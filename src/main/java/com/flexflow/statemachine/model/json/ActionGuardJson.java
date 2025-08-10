package com.flexflow.statemachine.model.json;

import lombok.Data;
import java.util.Map;

/**
 * Represents an action or a guard block within the state machine definition JSON.
 */
@Data
public class ActionGuardJson {
    /**
     * The name of the action or guard bean to be invoked.
     * This must match the name returned by the getName() method of a DynamicAction or DynamicGuard implementation.
     */
    private String name;

    /**
     * Optional parameters to be passed to the action or guard's execute/evaluate method.
     */
    private Map<String, Object> params;
}
