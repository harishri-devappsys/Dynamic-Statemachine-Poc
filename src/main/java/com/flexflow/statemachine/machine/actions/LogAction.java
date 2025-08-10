package com.flexflow.statemachine.machine.actions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * A simple, reusable action that logs a message to the console.
 * The message can be customized via parameters in the JSON definition.
 */
@Component
@Slf4j
public class LogAction implements DynamicAction {

    @Override
    public String getName() {
        return "logAction"; // This name must match the 'name' that is specifed in the json.
    }

    @Override
    public void execute(StateContext<String, String> context, Map<String, Object> params) {
        String message = (String) params.getOrDefault("message", "Executing LogAction");
        String entityId = (String) context.getMessageHeader("entityId");

        log.info("[LogAction] EntityID: '{}' | State: {} -> {} | Event: {} | Message: '{}'",
                entityId,
                context.getSource().getId(),
                context.getTarget().getId(),
                context.getEvent(),
                message
        );
    }
}