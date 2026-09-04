package com.laughingenigma.customer_service.service;

import com.laughingenigma.customer_service.dto.CustomerValidationResponse;
import com.laughingenigma.customer_service.entity.Customer;
import com.laughingenigma.customer_service.entity.CustomerStatus;
import com.laughingenigma.customer_service.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer validateCustomer(String username) {
        //check 1
        boolean valid = (username != null && !username.isBlank());

        if(!valid) {
            return null;
        }

        //check 2
        Optional<Customer> customerOpt = customerRepository.findByUsername(username);
        Customer customer = customerOpt.orElse(null);
        if(customerOpt.isPresent()) {

            //check 3
            valid = customer.getStatus() == CustomerStatus.ACTIVE;
        }
        else {
            valid = false;
        }

        return valid ? customer : null;
    }
}
