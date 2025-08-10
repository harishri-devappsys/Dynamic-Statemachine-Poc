package com.flexflow.statemachine.machine.guards;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * A reusable guard that checks if a specific header in the message context
 * has an expected value.
 */
@Component
@Slf4j
public class HeaderValueGuard implements DynamicGuard {

    @Override
    public String getName() {
        return "headerValueGuard"; // This name must match the 'name' in the JSON definition.
    }

    @Override
    public boolean evaluate(StateContext<String, String> context, Map<String, Object> params) {
        String headerName = (String) params.get("headerName");
        Object requiredValue = params.get("requiredValue");

        if (headerName == null || requiredValue == null) {
            log.warn("[HeaderValueGuard] 'headerName' or 'requiredValue' not provided in params. Denying transition.");
            return false;
        }

        Object actualValue = context.getMessageHeader(headerName);
        boolean result = Objects.equals(actualValue, requiredValue);

        log.info("[HeaderValueGuard] Evaluating guard. Header: '{}', Required: '{}', Actual: '{}'. Result: {}",
                headerName, requiredValue, actualValue, result);

        return result;
    }
}