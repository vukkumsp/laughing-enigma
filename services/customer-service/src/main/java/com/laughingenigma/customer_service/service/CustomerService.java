package com.laughingenigma.customer_service.service;

import com.laughingenigma.customer_service.dto.CustomerValidationResponse;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    public boolean validateCustomer(String username) {
        boolean valid = username != null
                && !username.isBlank();
        return valid;
    }
}
