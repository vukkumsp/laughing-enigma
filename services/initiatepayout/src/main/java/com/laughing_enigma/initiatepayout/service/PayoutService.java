package com.laughing_enigma.initiatepayout.service;

import com.laughing_enigma.initiatepayout.dto.PayoutInitiateRequestDTO;
import com.laughing_enigma.initiatepayout.dto.PayoutInitiateResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

@Service
public class PayoutService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public PayoutService(KafkaTemplate<String, String> ktemplate, @Value("initiatepayout.topic") String t) {
        kafkaTemplate = ktemplate;
        topic = t;
    }

    public PayoutInitiateResponseDTO initiateExternalPayout(PayoutInitiateRequestDTO payload) {
        String requestId = UUID.randomUUID().toString();
        String value;

        try {
            value = new ObjectMapper().writeValueAsString(payload);
        }
        catch(JacksonException e) {
            throw new RuntimeException("Failed to serialize payout request");
        }
        kafkaTemplate.send(topic, requestId, value);
        return new PayoutInitiateResponseDTO(requestId, "PENDING", Instant.now().toString());
    }

}
