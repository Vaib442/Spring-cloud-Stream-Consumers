package com.example.springkinesisdata.kinesis.consumer;

import org.springframework.cloud.stream.binding.BindingsLifecycleController;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

import com.example.springkinesisdata.kinesis.dto.UserEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class KinesisConsumer {
    private final BindingsLifecycleController bindingsLifecycleController;

    @Bean
    public Consumer<org.springframework.messaging.Message<byte[]>> kinesisSink() {
        return this::processMessage;
    }

    public void processMessage(Message<byte[]> event) {
        try {
            UserEvent userEvent = new ObjectMapper().readValue(new String(event.getPayload()), UserEvent.class);
            log.info("Incoming event => {}", userEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @ServiceActivator(inputChannel = "test_input_stream.test_input_stream_records.errors")
    public void handleErrors(ErrorMessage message) {
        bindingsLifecycleController.changeState("kinesisSink-in-0", BindingsLifecycleController.State.STOPPED);
        bindingsLifecycleController.changeState("kinesisSink-in-0", BindingsLifecycleController.State.STARTED);
    }

}
