package com.laughing_enigma.initiatepayout.controller;

import com.laughing_enigma.initiatepayout.dto.PayoutInitiateRequestDTO;
import com.laughing_enigma.initiatepayout.dto.PayoutInitiateResponseDTO;
import com.laughing_enigma.initiatepayout.service.PayoutService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payout")
public class PayoutController {
    private final PayoutService payoutService;

    public PayoutController(PayoutService ps) {
        payoutService = ps;
    }

    @PostMapping(value = "/initiate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PayoutInitiateResponseDTO> initiateExternalPayout(@Valid @RequestBody PayoutInitiateRequestDTO payload) {
        System.out.println("entered");
        PayoutInitiateResponseDTO response = payoutService.initiateExternalPayout(payload);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/test")
    String test(){
        return "returns test";
    }
}
