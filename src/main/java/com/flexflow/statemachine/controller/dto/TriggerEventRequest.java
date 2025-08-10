package com.flexflow.statemachine.controller.dto;

import lombok.Data;
import java.util.Map;

@Data
public class TriggerEventRequest {
    private String event;
    private Map<String, Object> headers;
}