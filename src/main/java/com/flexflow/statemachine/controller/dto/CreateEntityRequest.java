package com.flexflow.statemachine.controller.dto;

import lombok.Data;

@Data
public class CreateEntityRequest {
    private String businessId;
    private String machineId;
}