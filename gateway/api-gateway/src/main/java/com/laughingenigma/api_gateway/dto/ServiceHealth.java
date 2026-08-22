package com.laughingenigma.api_gateway.dto;

import java.util.Map;

public record ServiceHealth(
        String status,
        Map<String, Object> components
) {}