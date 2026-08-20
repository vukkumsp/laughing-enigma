package com.laughingenigma.customer_service.dto;

public class Profile {
    public String username;
    public String firstname;
    public String lastname;

    public Profile() {
    }
    public Profile(String username, String firstname, String lastname) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
    }
}
