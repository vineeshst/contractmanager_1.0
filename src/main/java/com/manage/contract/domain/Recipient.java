package com.manage.contract.domain;

public class Recipient {
    private String email;

    public Recipient() {
    }

    public Recipient(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
