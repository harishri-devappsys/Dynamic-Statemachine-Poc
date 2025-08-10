package com.flexflow.statemachine.controller;

import com.flexflow.statemachine.controller.dto.CreateEntityRequest;
import com.flexflow.statemachine.controller.dto.TriggerEventRequest;
import com.flexflow.statemachine.model.db.StatefulEntity;
import com.flexflow.statemachine.repository.StatefulEntityRepository;
import com.flexflow.statemachine.service.core.DynamicStateMachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 * REST Controller to expose the dynamic state machine functionality.
 */
@RestController
@RequestMapping("/api/entities")
@RequiredArgsConstructor
public class ApiController {

    private final DynamicStateMachineService stateMachineService;
    private final StatefulEntityRepository entityRepository;

    /**
     * Creates a new business entity managed by a state machine.
     */
    @PostMapping
    public ResponseEntity<?> createEntity(@RequestBody CreateEntityRequest request) {
        try {
            StatefulEntity entity = stateMachineService.createEntity(request.getBusinessId(), request.getMachineId());
            return ResponseEntity.ok(entity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Retrieves the current state of a business entity.
     */
    @GetMapping("/{businessId}")
    public ResponseEntity<StatefulEntity> getEntity(@PathVariable String businessId) {
        return entityRepository.findByBusinessId(businessId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Triggers an event on a business entity.
     */
    @PostMapping("/{businessId}/trigger")
    public ResponseEntity<?> triggerEvent(@PathVariable String businessId, @RequestBody TriggerEventRequest request) {
        try {
            boolean success = stateMachineService.triggerEvent(
                    businessId,
                    request.getEvent(),
                    request.getHeaders() != null ? request.getHeaders() : Collections.emptyMap()
            );
            return ResponseEntity.ok(Map.of("success", success));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}