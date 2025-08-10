package com.flexflow.statemachine.service.persister;

import com.flexflow.statemachine.model.db.StatefulEntity;
import com.flexflow.statemachine.repository.StatefulEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;

/**
 * Implements the persistence mechanism for the state machine.
 * It reads and writes the state of a StatefulEntity to/from the database.
 */
@Component
@RequiredArgsConstructor
public class DynamicStateMachinePersister implements StateMachinePersist<String, String, String> {

    private final StatefulEntityRepository entityRepository;

    @Override
    public void write(StateMachineContext<String, String> context, String businessId) throws Exception {
        StatefulEntity entity = entityRepository.findByBusinessId(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Could not find entity with businessId: " + businessId));

        entity.setCurrentState(context.getState());
        entityRepository.save(entity);
    }

    @Override
    public StateMachineContext<String, String> read(String businessId) throws Exception {
        StatefulEntity entity = entityRepository.findByBusinessId(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Could not find entity with businessId: " + businessId));

        return new DefaultStateMachineContext<>(entity.getCurrentState(), null, null, null, null, entity.getMachineId());
    }
}