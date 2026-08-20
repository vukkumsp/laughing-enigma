package com.laughingenigma.customer_service.dto;

public class Profile {
    public String username;
    public String firstname;
    public String lastname;
    public boolean valid;
    public Profile(String username, String firstname, String lastname, boolean valid) {
        this.username = username;
        this.valid = valid;
    }

    public Profile() {
        this.valid = false;
    }
    public Profile(String username, String firstname, String lastname) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.valid = true;
    }
}
