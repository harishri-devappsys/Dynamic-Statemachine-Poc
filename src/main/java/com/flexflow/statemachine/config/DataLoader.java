package com.flexflow.statemachine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexflow.statemachine.model.db.StateMachineDefinition;
import com.flexflow.statemachine.model.json.StateMachineDefinitionJson;
import com.flexflow.statemachine.repository.StateMachineDefinitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Loads initial data into the database on application startup.
 * This is a more robust alternative to using data.sql for complex data like JSON.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final StateMachineDefinitionRepository definitionRepository;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        // Define the machineId we want to load
        String machineId = "clarification_lifecycle_v1";

        if (definitionRepository.findByMachineId(machineId).isEmpty()) {
            log.info("No definition found for machineId '{}'. Loading from JSON file.", machineId);

            Resource resource = resourceLoader.getResource("classpath:clarification-lifecycle.json");
            try (InputStream inputStream = resource.getInputStream()) {
                // Read the file content as a string
                String jsonContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                // Validate that it's the correct definition
                StateMachineDefinitionJson parsedJson = objectMapper.readValue(jsonContent, StateMachineDefinitionJson.class);
                if (!machineId.equals(parsedJson.getMachineId())) {
                    throw new IllegalStateException("The machineId in the JSON file does not match the expected machineId.");
                }

                // Create and save the entity
                StateMachineDefinition newDefinition = new StateMachineDefinition();
                newDefinition.setMachineId(machineId);
                newDefinition.setDefinitionJson(jsonContent);
                definitionRepository.save(newDefinition);
                log.info("Successfully loaded and saved state machine definition for '{}'.", machineId);
            }
        } else {
            log.info("State machine definition for '{}' already exists in the database. Skipping data load.", machineId);
        }
    }
}