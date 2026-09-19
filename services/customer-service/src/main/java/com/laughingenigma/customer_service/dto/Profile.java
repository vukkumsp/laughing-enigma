package com.laughingenigma.customer_service.dto;

import com.laughingenigma.customer_service.entity.Customer;

public class Profile {
    public String username;
    public String firstName;
    public String lastName;

    public Profile(Customer customer) {
        this.username = customer.getUsername();
        this.firstName = customer.getFirstName();
        this.lastName = customer.getLastName();
    }

    public Profile(String username, String firstName, String lastName) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
