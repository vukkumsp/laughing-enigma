package com.laughing_enigma.initiatepayout.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PayoutInitiateResponseDTO {
    private String requestId;
    private String status;
    private String initiatedAt;
}
